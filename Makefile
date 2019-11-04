all:
	mvn install

test-resource/%.class: test-resource/%.java
	javac $^

test: test-resource/DemoClass.class
	mvn -Dtest=AppTest test
