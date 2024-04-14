from evdev import InputDevice, categorize, ecodes

# Creates an object 'gamepad' to store the data
gamepad = InputDevice('/dev/input/event2')

# Prints out device info at start
print(gamepad)

# Evdev takes care of polling the controller in a loop
for event in gamepad.read_loop():
    if event.type == ecodes.EV_ABS:
        # Handling absolute position events
        abs_event = categorize(event)
        input_type = ecodes.ABS[abs_event.event.code]
        input_value = (abs_event.event.value - 32767.5)
        print(f"{input_type}: {input_value}")
    elif event.type == ecodes.EV_KEY:
        # Handling key press events
        key_event = categorize(event)
        input_type = ecodes.KEY[key_event.event.code] if key_event.event.code in ecodes.KEY else 'Unknown Key'
        input_value = key_event.event.value  # 1 for key press, 0 for key release
        print(f"{input_type}: {'Pressed' if input_value else 'Released'}")
    # Add more elif blocks here to handle other types of events.
