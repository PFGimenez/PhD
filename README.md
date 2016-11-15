## This is the git repository of a PhD student, Pierre-François Gimenez, working at IRIT.

_My work is based on the SLDD compilator SALADD by Nicolas Schmidt._

## Interactive recommender demonstrator

A [Dockerfile](https://github.com/PFgimenez/thesis/blob/recoDemoServer/demonstrateur/Dockerfile) is available to build the interactive recommender demonstrator.

To build the image : (this may take a few minutes)

    sudo docker build -t recodemoserver https://raw.githubusercontent.com/PFgimenez/thesis/recoDemoServer/demonstrateur/Dockerfile

Once the image is built, you can start the server :

    sudo docker run -d -p 80:80 recodemoserver

To stop the server :

    sudo docker stop $(sudo docker ps -q --filter ancestor=recodemoserver)

Internet access is needed to build the image, but not to run it.

When the server is started, you can access it with a browser at the adress http://127.0.0.1

Only one instance of the server can run at the same time.

### Troubleshooting

Docker build can fail if you are behind intranet. The error with look like :

    Could not resolve 'security.debian.org'

This is a DNS error. First, find the DNS server address you use :

    nm-tool | grep DNS
    
At the end of the file /etc/default/docker, add the line (replace with your actual DNS address) :

    DOCKER_OPTS="--dns 1.2.3.4"
    
Restart the docker deamon :

    sudo service docker restart
    
You can build the image again using :

    sudo docker build --no-cache=true -t recodemoserver https://raw.githubusercontent.com/PFgimenez/thesis/recoDemoServer/demonstrateur/Dockerfile
