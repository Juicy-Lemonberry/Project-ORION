import inputs
import pantilthat as p
def main():
    r = 0
    g = 0
    b = 0
    pan_angle = 0
    tilt_angle = 0
    p.pan(pan_angle)
    p.tilt(tilt_angle)
    p.clear()
    p.light_mode(1) #1 = WS2812
    p.light_type(1) #1 = GRB
    p.set_all(r,g,b) #turn off all LEDs
    p.show() #flush the buffer to the LEDs

    print("Press buttons on your Xbox controller. Press Ctrl+C to quit.")
    try:
        while True:
            events = inputs.get_gamepad()
            for event in events:
                if event.ev_type == "Absolute" or event.ev_type == "Key": 
                    if (event.code == "ABS_X" or event.code == "ABS_Y" or event.code == "ABS_RX" or event.code == "ABS_RY") and (event.state < 5000 and event.state > -5000):
                        continue
                    #print(f"\n1. EV TYPE:{event.ev_type}, \n2. EVENT.CODE:{event.code}, \n3. EVENT.STATE:{event.state}")
                    if event.code == "ABS_X":
                        pan_angle = pan_angle - int(event.state/3276.7)
                        if pan_angle > 90:
                            pan_angle = 90
                        if pan_angle < -90:
                            pan_angle = -90
                        #print(f"pan_angle: {pan_angle}")
                        p.pan(pan_angle)
                    if event.code == "ABS_Y":
                        tilt_angle = tilt_angle + int(event.state/3276.7)
                        if tilt_angle > 90:
                            tilt_angle = 90
                        if tilt_angle < -90:
                            tilt_angle = -90
                        print(f"tilt_angle: {tilt_angle}")
                        p.tilt(tilt_angle)

                    if event.code == "BTN_THUMBL" and event.state == 1:
                        #print("BTN_THUMBL - Resetting pan and tilt")
                        pan_angle = 0
                        tilt_angle = 0
                        p.pan(pan_angle)
                        p.tilt(tilt_angle)

                    if event.code == "BTN_WEST":
                        r = 255 * event.state
                        g = 255 * event.state
                        b = 255 * event.state
                        

                    if event.code == "BTN_SOUTH":
                        g = 255 * event.state

                    if event.code == "BTN_EAST":
                        r = 255 * event.state

                    if event.code == "BTN_NORTH":
                        b = 255 * event.state

                    p.set_all(r,g,b)
                    p.show()

    except KeyboardInterrupt:
        print("\nExiting...")

if __name__ == "__main__":
    main()
