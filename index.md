# PhD on preferences learning and recommendation under constraints

_My work is based on the SLDD compilator [SALADD](https://github.com/SchmidtNicolas/SALADD) by Nicolas Schmidt._

## Interactive recommender demonstrator

An interactive recommender demonstrator is available. It needs [docker](https://docs.docker.com/engine/installation/) to run.

To build the image : (this may take a few minutes)

    $ sudo docker build --no-cache=true -t recodemoserver https://raw.githubusercontent.com/PFgimenez/thesis/recoDemoServer/demonstrateur/Dockerfile

Once the image is built, you can start the server :

    $ sudo docker run -d --name instancereco -p 80:80 recodemoserver

To stop the server :

    $ sudo docker stop instancereco && sudo docker rm instancereco

Some remarks :

- When the server is started, you can access it with a browser at the adress http://127.0.0.1
- Internet access is needed to build the image, but not to run it.
- Only one instance of the server can run at the same time.
- If you want to upgrade the server, you need to build it again (with the same line as above).

### Troubleshooting

#### 32 bits system

Use this link to build the image :

    $ sudo docker build --no-cache=true -t recodemoserver https://raw.githubusercontent.com/PFgimenez/thesis/recoDemoServer/demonstrateur/Dockerfile-32bits
    
The other steps aren't changed.

#### DNS problem

Docker build can fail if you are behind intranet. The error will look like :

    Could not resolve 'security.debian.org'

This is a DNS error. First, find the DNS server address you use :

    $ nm-tool | grep DNS
    
At the end of the file /etc/default/docker, add the line (replace with your actual DNS address) :

    DOCKER_OPTS="--dns 1.2.3.4"
    
Restart the docker deamon :

    $ sudo service docker restart
    
You can build the image.
