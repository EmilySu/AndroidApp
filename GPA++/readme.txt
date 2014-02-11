Full project:
Compilation:
- Load the GPA++ project in Eclipse and run the project as an Android application. This will also install the app (.apk file) into the Android device.

Execution:
- Once the app is installed (.apk file loaded on the actual device or AVD), the app can be tapped to be executed. The minimum required API level is 14, and it is target API level is 18.

Notes:
- The app cannot be supported by devices with small screens, but it will support normal and large screens (alternative layout), and this is reflected in the manifest file.

Directory:
- GPA++ directory contains our source code
- database.pdf is the UML drawing for our database schema
- business_model_canvas_poster_teamA+.pdf is our business model canvas
- Design 1.jpg and Design 2.jpg contain our rough design drawings. The formal design is in Unit 4.
- classDiagram.png contains our overall class diagram.
- DB_Improvements.txt contains a possible improvement to our database design to increase our app performance



Previous notes:
Unit 4 - Construction Phase 1
Progress:
- Database (DB) layer complete: it can perform all CRUD operations of the designed DB schema.
- Application layer has basic methods for retrieving data from DB layer and providing application logic to presentation layer
- Presentation layer basic look and feel is complete: users can navigate to all UIs of our project, but the functionality may not be present. For example, users can swipe from our "Today" tab to "Classes" tab, but once tapping on adding a class and tapping on the ok button, the classes is not saved in the DB. In addition, the lists in our ListViews are static dummy date, not real data from the database.

To-Dos:
- Complete Application layer
- Integrate Presentation layer, application layer and database layer
- Adding camera, voice recorder feature
- Adding swiping gesture to listView to delete to-do tasks
- Add GPS functionality to view map location
- Add data sharing to Facebook or Twitter
- Add events to CalendarView
- Add reminder notifications