# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Map movement

Testing if i can successfully move from different locations throughout the map

### Test Data To Use

I'll put some println()s with the code that changes the current location, and then attempt to move locations by clicking the panels

### Expected Test Result

the player icon should move, the currentLocation variable should change to the appropriate location, and we should get the appropriate location printed out

---

## changing background test (valid)

the background should change when i click on the appropriate areas

### Test Data To Use

I will click on the appropriate areas and have some println()s showing the index of the background, and the location its at

### Expected Test Result

the background should change and print out the currentLocation and background index

---

## changing background test (invalid)

the background shouldn't change when i click on its designated area because i haven't met the requirements to activate it

### Test Data To Use

I'll click on the 2nd background of the breaker box location when i dont have the wire cutters

### Expected Test Result

the wire shouldnt cut because i dont have the wire cutters item, and the inner monologue should have new dialogue telling us that we dont meet the requirements

---

## changing background test (boundary)

because i have the wire cutters item i should be able to cut through the blue wire

### Test Data To Use

I'll obtain the wire cutters item and attempt to change the background from the second to the third in the breaker box

### Expected Test Result

the background should change and the wire cutters should be used up, indicated by the inner monologue

---

## changing background without interacting with them

when the blue wire is cut, it should change the background index of the room with the security officer, appearing to open the door

### Test Data To Use

ill start at the security officers door, head over to the breaker and cut the blue wire and move back to the security officers door, and see if its changed

### Expected Test Result

it should start off closed, and when i go cut the wire it should be open.

---

## Example Test Name

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Example Test Name

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---


