import bpy, json, os


HOLD_ON_LAST_FRAME = True
file_name = "super_dash"

# Export path here i.e C:\Users\user\Desktop
# Exports to model's directory if path is empty or invalid
EXPORT_PATH = r""

# Add "version", "_comments", "uuid" metadatas to this dictionary
main_file_dict = {
    "name": file_name,
    "author": "goatee",
    "description": "description",
    "emote": {
        "beginTick": bpy.context.scene.frame_start,
        "endTick": 0,  # Calculated on run
        "stopTick": bpy.context.scene.frame_end + 1,
        "isLoop": False,
        "returnTick": 0,
        "nsfw": False,
        "degrees": False,
        "moves": [],
    },
}

LEGS_Z_OFFSET = 3
data_sort_order = ["x", "y", "z", "pitch", "yaw", "roll", "bend_data"]
partdata = {}
endtick = 0


def correctValue(value, name, type, fcurve):
    if name == "head" and type == "y":
        value -= 3

    if (name == "rightLeg" or name == "leftLeg") and type == "y":
        value -= LEGS_Z_OFFSET

    if fcurve.data_path == "location":
        if not name == "torso":
            value = value * -4
        else:
            value = value * 0.25
            if type == "z":
                value = value * -1
    elif not (name == "torso" and not (type == "roll" or type == "bend")) and not (
        type == "axis"
    ):  # rotation correction (*-1) except for torzo roll/bend
        value = value * -1

    if type == "y":
        if name == "rightLeg" or name == "leftLeg":
            value += 12

    if type == "z" or type == "x":
        if name == "rightLeg":
            value += 0.1
        elif name == "leftLeg":
            value -= 0.1

    if name == "rightArm" or name == "leftArm":
        if type == "y":
            value += 12

    return value


def getPartData(name):
    global partdata
    if (
        name in bpy.data.objects
        and bpy.data.objects[name].animation_data is not None
        and bpy.data.objects[name].animation_data.action is not None
    ):
        for fcurve in bpy.data.objects[name].animation_data.action.fcurves:

            if fcurve.data_path == "location":
                if fcurve.array_index == 0:
                    type = "x"
                elif fcurve.array_index == 1:
                    type = "z"
                elif fcurve.array_index == 2:
                    type = "y"

            elif "rotation_euler" in fcurve.data_path:
                if fcurve.array_index == 0:
                    type = "pitch"
                elif fcurve.array_index == 1:
                    type = "roll"
                elif fcurve.array_index == 2:
                    type = "yaw"
            else:
                continue

            for keyframe in fcurve.keyframe_points:
                frame = int(keyframe.co[0])
                value = float(keyframe.co[1])
                if frame == 0:
                    continue

                if name not in partdata:
                    partdata[name] = {}
                if frame not in partdata[name]:
                    partdata[name][frame] = {}

                partdata[name][frame][type] = correctValue(value, name, type, fcurve)

    ###########################################################################
    ###########################################################################
    # Bending data
    bendName = name + "_bend"

    if (
        bendName in bpy.data.objects
        and bpy.data.objects[bendName].animation_data is not None
        and bpy.data.objects[bendName].animation_data.action is not None
    ):
        for fcurve in bpy.data.objects[bendName].animation_data.action.fcurves:
            if fcurve.data_path == "rotation_euler":
                if fcurve.array_index == 0:
                    type = "bend"
                elif fcurve.array_index == 1:
                    type = "axis"

                for keyframe in fcurve.keyframe_points:
                    frame = int(keyframe.co[0])
                    value = float(keyframe.co[1])
                    if frame == 0:
                        continue

                    if name not in partdata:
                        partdata[name] = {}
                    if frame not in partdata[name]:
                        partdata[name][frame] = {}
                    if "bend_data" not in partdata[name][frame]:
                        partdata[name][frame]["bend_data"] = {}

                    partdata[name][frame]["bend_data"][type] = correctValue(
                        value, name, type, fcurve
                    )


def getKeyframeObj(name, frame):
    if name not in bpy.data.objects or bpy.data.objects[name].animation_data is None:
        return None

    action = bpy.data.objects[name].animation_data.action
    if action is None:
        return None

    for fcurve in action.fcurves:
        for keyframe in fcurve.keyframe_points:
            if int(keyframe.co[0]) == frame:
                return keyframe


def getEasing(keyframe):
    if keyframe is None:
        return "EASEINOUTQUAD"

    if keyframe.easing == "AUTO":
        easing = "EASEINOUT"
    else:
        easing = "".join(keyframe.easing.split("_"))
    if keyframe.interpolation == "BEZIER":
        easing = easing + "QUAD"
    else:
        if not (
            keyframe.interpolation == "CONSTANT" or keyframe.interpolation == "LINEAR"
        ):
            easing = easing + str(keyframe.interpolation)
        else:
            easing = str(keyframe.interpolation)
    return easing


getPartData("head")
getPartData("torso")
getPartData("rightArm")
getPartData("leftArm")
getPartData("rightLeg")
getPartData("leftLeg")

for name, frames in partdata.items():
    frames = dict(sorted(frames.items()))
    for frame, data in frames.items():
        data = {key: data[key] for key in data_sort_order if key in data}  # Sorts data
        frame_output = {
            "tick": frame,
            "easing": getEasing(getKeyframeObj(name, frame)),
            "turn": 0,
        }

        bend_data = {}
        if "bend_data" in data:
            bend_data = data.pop("bend_data")

        if len(data) > 0:
            frame_output.update({name: data})
            main_file_dict["emote"]["moves"].append(frame_output)

        if len(bend_data) > 0:
            data = {
                key: data[key] for key in ["bend", "axis"] if key in data  # Sorts data
            }

            bend_output = {
                "tick": frame,
                "easing": getEasing(getKeyframeObj(name + "_bend", frame)),
                "turn": 0,
            }
            bend_output.update({name: bend_data})
            main_file_dict["emote"]["moves"].append(bend_output)

        if frame > endtick:
            endtick = frame


main_file_dict["emote"]["endTick"] = endtick
if HOLD_ON_LAST_FRAME:
    main_file_dict["emote"]["isLoop"] = True
    main_file_dict["emote"]["returnTick"] = endtick

if not os.path.isdir(EXPORT_PATH) or not EXPORT_PATH:
    EXPORT_PATH = os.path.dirname(bpy.data.filepath)

output_file = os.path.join(EXPORT_PATH, f"{file_name}.json")
with open(output_file, "w") as f:
    json.dump(main_file_dict, f, indent=4)


def ShowMessageBox(title="Message Box", message="", icon="CHECKMARK"):
    def draw(self, context):
        self.layout.label(text=message)

    bpy.context.window_manager.popup_menu(draw, title=title, icon=icon)


ShowMessageBox(f"Successfully created {file_name}!", f"Exported to {output_file}")

GREEN = "\033[92m"
print(f"{GREEN}Successfully created {file_name}!", f"Exported to {output_file}")
