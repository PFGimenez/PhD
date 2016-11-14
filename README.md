## This is the git repository of a PhD student, Pierre-François Gimenez, working at IRIT.

_My work is based on the SLDD compilator SALADD by Nicolas Schmidt._


A [Dockerfile](https://github.com/PFgimenez/thesis/blob/recoDemoServer/demonstrateur/Dockerfile) is available to build the interactive recommender demonstrator.

To build the image : (this may take a few minutes)

    sudo docker build -t recodemoserver https://raw.githubusercontent.com/PFgimenez/thesis/recoDemoServer/demonstrateur/Dockerfile

To start the server :

    sudo docker run -d -p 80:80 recodemoserver

To stop the server :

    sudo docker stop $(sudo docker ps -q --filter ancestor=recodemoserver)


When the server is started, you can access it with a browser at the adress http://127.0.0.1

Only one instance of the server can run at the same time.
