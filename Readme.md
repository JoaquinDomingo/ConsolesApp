# Consoles App

## Project Overview

This app is a mobile application built using Kotlin for Android Devices. 

In the initial versios it will provide users 
with a comprehensive catalog of gaming consoles, allowing them 
to browse through various consoles, view a bit of information which will be expanded with a drop-down menu (this will be available
in further versions, and manage their favorite consoles). 

Besides that, users can also add new consoles to the catalog, ensuring that 
the app remains up-to-date with the latest releases. In future versions, users will be able to edit and delete consoles as well, furthermore,
the functions of seeing related games to each console will be added, beeing it either games that are compatible with the console or games that 
were released for it.

## Features

- Browse a catalog of gaming consoles
- View detailed information about each console
- Add new consoles to the catalog
- Manage a list of favorite consoles
- Future updates will include editing and deleting consoles
- Future updates will include viewing related games for each console
- User authentication and profile management (planned for future versions)
- Social sharing features (planned for future versions)

## Technologies Used

- Kotlin
- Android SDK
- SQLite for local data storage (not yet implemented)
- Retrofit for network operations (planned for future versions)
- Firebase for user authentication (planned for future versions)
- MVC Architecture that will be migrated to MVVM in future versions
- Activities for different screens and their respective layouts
- RecyclerView for displaying lists of consoles
- Intents for navigation between activities (not yet implemented)


## Architecture 

The app follows the Model-View-Controller (MVC) architecture pattern, which separates 
the application logic into three interconnected components:

- Model: Represents the data and business logic of the application. In this app, the Model includes classes that define the structure 
    of a gaming console and handle data operations. The class Console is located in the 'model' package.

- View: Represents the user interface of the application. The View is responsible for displaying data to the user and capturing user input.
- In this app, the View consists of XML layout files that define the UI components for different screens, such as the main activity layout and 
  the console detail layout. These files are located in the 'res/layout' directory. In addition, the RecyclerView is used to display 
  lists of consoles in a scrollable format. In future versions, the View will also include UI components for user authentication and profile management.

- Controller: Acts as an intermediary between the Model and the View. The Controller handles user input, 
  updates the Model, and refreshes the View. In this app, the Controller includes Activities that manage the interactions between the user and the application. 
  The MainActivity is responsible for displaying the list of consoles.

- Adapters: The app uses Adapters to bind data from the Model to the View components, particularly for displaying lists in RecyclerViews. 
  The ConsoleAdapter is responsible for populating the RecyclerView with console data. ViewHConsole is used to hold the views for each console item in the RecyclerView.

## Future Improvements

- Implement user authentication and profile management using Firebase.
- Add functionality to edit and delete consoles from the catalog.
- Integrate a backend service to fetch console data and related games.

- Migrate from MVC to MVVM architecture for better separation of concerns and easier testing.


## Author

- Joaquin Domingo Domingo
- Email: dojoaquindo@gmail.com
- GitHub: [JoaquinDomingo]

## Other Information
- This project is an educational project and is not intended for commercial use. It is being developed to enhance my skills in Android development using Kotlin
    for a class project. 