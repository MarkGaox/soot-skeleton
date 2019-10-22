all:
	javac test-resource/DemoClass.java
	mvn install

test:
	mvn -Dtest=AppTest test
