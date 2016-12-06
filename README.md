# PhD on preferences learning and recommendation under constraints

_My work is based on the SLDD compilator [SALADD](https://github.com/SchmidtNicolas/SALADD) by Nicolas Schmidt._

## Interactive recommender demonstrator

An interactive recommender demonstrator is available. It needs [docker](https://docs.docker.com/engine/installation/) to run.

A docker image is available at https://hub.docker.com/r/pfgimenez/reco-demo/.

Once the image is built, you can start the server :

    $ sudo docker run -d --name instancereco -p 80:80 pfgimenez/reco-demo

To stop the server :

    $ sudo docker stop instancereco && sudo docker rm pfgimenez/reco-demo

Some remarks :

- When the server is started, you can access it with a browser at the adress http://127.0.0.1
- Internet access isn't needed to run the demonstrator.
- Only one instance of the server can run at the same time.
