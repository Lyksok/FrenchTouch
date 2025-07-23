# SE1 Project

Team : French Touch <br>
Members : [Lyksok](https://github.com/Lyksok), [Yoplyy](https://github.com/Yoplyy)

Welcome to French Touch, a dynamic music streaming application designed to connect users with their favorite artists and discover new music seamlessly. Our platform offers a rich set of features tailored for users, artists, and administrators, ensuring a comprehensive and engaging music experience.

# Configuration
Before launching the application, you need to setup the `app.config` file with the correct server URL and PORT. Here is an example of configuration file :
```
SERVER_ADDR=127.0.0.1
SERVER_PORT=50000
```

# Server
The French Touch client can connect to a database written in Rust using HTTPS connection. Here is the link of the repo : [link](https://github.com/Lyksok/FrenchTouchServer).
This project is made by student, thus, it does not implement all safety features needed for public distribution. However, password are stored safely using hashes and are bruteforce and rainbow table proof. <br>

# Disclaimer
This project can contain some bugs and some non-working features. You can report them in an issue on GitHub.
