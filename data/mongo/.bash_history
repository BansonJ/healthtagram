apt-get update
apt-get install vim -y
cd etc/
vi mongod.conf.orig
docker compose down
exit
mongo -u root -p 1234 --authenticationDatabase admin
mongo -u root -p 1234 --authenticationDatabase admin
mongosh -u root -p 1234 --authenticationDatabase admin
use healthtagram;
show collections;
use admin;
db.getUsers();
use admin;
mongosh --host 127.0.0.1 --port 27017 -u root -p 1234 --authenticationDatabase admin
exit
apt-get update
apt-get install vim -y
ls
vi etc/mongod.conf.orig
docker exec -it mongodb mongosh --port 27017
exit
