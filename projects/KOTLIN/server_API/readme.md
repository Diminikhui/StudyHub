1.
./gradlew clean run

2.
./gradlew clean test

3.
curl -i http://localhost:8080/

4.
curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
  
  5.
  TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
	-H "Content-Type: application/json" \
	-d '{"username":"teacher","password":"password123"}' \
	| python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")
  
  echo "$TOKEN"
  
  6. 
  curl -i http://localhost:8080/events
  
  7. 
  EVENT_ID=$(curl -s -X POST http://localhost:8080/events \
	-H "Authorization: Bearer $TOKEN" \
	-H "Content-Type: application/json" \
	-d '{"title":"Exam event","description":"created by teacher"}' \
	| python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
  
  echo "EVENT_ID=$EVENT_ID"
  
  8.
  curl -i -X PUT "http://localhost:8080/events/$EVENT_ID" \
	-H "Authorization: Bearer $TOKEN" \
	-H "Content-Type: application/json" \
	-d '{"title":"Exam event updated","description":"updated"}'
	
9.
	curl -i -X DELETE "http://localhost:8080/events/$EVENT_ID" \
	  -H "Authorization: Bearer $TOKEN"
	  
10.
 curl -i http://localhost:8080/events
 
 11.
 curl -i -X POST http://localhost:8080/events \
   -H "Content-Type: application/json" \
   -d '{"title":"no token","description":"should fail"}'
   
   12. 
   # создать владельца A
   curl -s -X POST http://localhost:8080/auth/register \
	 -H "Content-Type: application/json" \
	 -d '{"username":"ownerA","password":"password123"}' >/dev/null
   
   TOKEN_A=$(curl -s -X POST http://localhost:8080/auth/login \
	 -H "Content-Type: application/json" \
	 -d '{"username":"ownerA","password":"password123"}' \
	 | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")
   
   EVENT_A=$(curl -s -X POST http://localhost:8080/events \
	 -H "Authorization: Bearer $TOKEN_A" \
	 -H "Content-Type: application/json" \
	 -d '{"title":"A event","description":"owned by A"}' \
	 | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
   
   # создать владельца B
   curl -s -X POST http://localhost:8080/auth/register \
	 -H "Content-Type: application/json" \
	 -d '{"username":"ownerB","password":"password123"}' >/dev/null
   
   TOKEN_B=$(curl -s -X POST http://localhost:8080/auth/login \
	 -H "Content-Type: application/json" \
	 -d '{"username":"ownerB","password":"password123"}' \
	 | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")
   
   # B пытается обновить событие A 
   curl -i -X PUT "http://localhost:8080/events/$EVENT_A" \
	 -H "Authorization: Bearer $TOKEN_B" \
	 -H "Content-Type: application/json" \
	 -d '{"title":"HACK","description":"should be forbidden"}'
   
   # B пытается удалить событие A 
   curl -i -X DELETE "http://localhost:8080/events/$EVENT_A" \
	 -H "Authorization: Bearer $TOKEN_B"
	 
13.
Ctrl + C