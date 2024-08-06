This project demonstrates how to create a BPMN process model using Camunda BPMN Modeler that fetches a picture based on a user’s selection (cat, dog, or bear). I used Camunda SaaS for this exercise. I also built a spring boot client app that will deploy the process model to a Camunda installation and will include Job worker and test classes.

# Deploying a BPMN Process Model to Camunda 8

## Prerequisites

- [Camunda SaaS](https://camunda.com/platform/) account
- Basic knowledge of BPMN and Camunda
- A running instance of Camunda Platform (local or hosted)

## Steps to Create the Process Model

### Step 1: Start Camunda Modeler

1. Open Camunda Modeler.
2. Create a new BPMN diagram.

### Step 2: Define the Start Event

1. Drag and drop a **Start Event** onto the canvas.
2. Name it **Start Process**.

### Step 3: Create a User Task for Selection

1. Drag and drop a **User Task** onto the canvas next to the Start Event.
2. Name it **Select Animal Type**.
3. Create an embedded form to capture the input. I used the dropdown field for animal type.
4. Connect the **Start Event** to the **User Task**.

### Step 4: Add an Exclusive Gateway

1. Drag and drop an **Exclusive Gateway** onto the canvas next to the User Task.
2. Name it **User's Selection**.
3. Connect the **User Task** to the **Exclusive Gateway**.

### Step 5: Create Service Tasks for Fetching Pictures

1. Drag and drop three **Rest Outbound Connectors** onto the canvas, one for each animal.
2. Name them **Cat API**, **Dog API**, and **Bear API** respectively.
3. Connect the **Exclusive Gateway** to each **Rest Outbound Connector**.

### Step 6: Define the End Event

1. Drag and drop an **End Event** onto the canvas.
2. Name it **End Process**.
3. Connect each **Rest Outbound Connector** to the **End Event**.

### Step 7: Configure the Exclusive Gateway

1. Select the **Exclusive Gateway**.
2. Define three outgoing flows from the **Exclusive Gateway**:
   - One to **Cat API** (Condition: `animalType == 'cat'`)
   - One to **Dog API** (Condition: `animalType == 'dog'`)
   - One to **Bear API** (Condition: `animalType == 'bear'`)

### Step 8: Configure the Service Tasks

1. Select each **Rest Outbound Connector** and configure the implementation details:
   - **Cat API**: Implementation URL - `http://placekitten.com/`
   - **Dog API**: Implementation URL - `https://random.dog/woof.json`
   - **Bear API**: Implementation URL - `http://placebear.com/`

### Step 9: Save and Deploy the Process Model

1. Save the BPMN diagram.
2. Deploy the process model to your Camunda Platform instance.

### Final Process model and BPMN XML

![image](https://github.com/user-attachments/assets/6fcbbe7c-bbae-4e15-ad04-cccbf052da3a)

You can find the generated BPMN XML [here](https://github.com/ankitsrivastava/camundaapp/blob/master/src/main/resources/random-animal.bpmn).

# Spring boot client App

I used [Spring Initializer](https://start.spring.io) to build a Spring boot app with dependencies such as JPA, Database, etc.

<img width="1261" alt="Screenshot 2024-08-06 at 9 46 51 AM" src="https://github.com/user-attachments/assets/d8af442b-5b41-4a46-91a5-d84f43736145">

I added dependencies related to Zeebe client API into pom.xml (Using JDK 17, so dependencies need to be compatible with the JDK version)

    <dependency>
      	<groupId>io.camunda</groupId>
      	<artifactId>zeebe-client-java</artifactId>
      	<version>${zeebe.version}</version>
    </dependency>
		<dependency>
		    <groupId>io.camunda</groupId>
		    <artifactId>spring-zeebe-starter</artifactId>
		    <version>${zeebe.version}</version>
		</dependency>
		<dependency>
  			<groupId>io.camunda</groupId>
  			<artifactId>zeebe-process-test</artifactId>
  			<version>1.3.0</version>
  			<scope>test</scope>
		</dependency>

## Deploy process models

Use the @Deployment annotation:

```
@SpringBootApplication
@Deployment(resources = "classpath:random-animal.bpmn")
public class AssignmentApplication{
```

## Start a process instance of the Camunda Process
I included this logic under the REST endpoint so that the process can be started when the endpoint gets hit.

```
client.newCreateInstanceCommand() 
        .bpmnProcessId("random-animal") 
        .latestVersion() 
        .variables(variables) 
        .send().join();
```
# Implement a Job worker

A job worker is a service capable of performing a particular task in a process. Each time such a task needs to be performed, this is represented by a job.

You will need to annotate your method with @JobWorker and type should be provided to refer to the task for which the job needs to be defined. You can write your business logic to execute. Please refer [AnimalJobWorker](https://github.com/ankitsrivastava/camundaapp/blob/master/src/main/java/com/ankit/camunda/work/assignment/AnimalWorker.java).

```
@JobWorker(type = "animalselect")
```
** Note: As I used Rest Outbound Connector, I was not able to find the type field so was not able to reference the job worker.**

# Add automated tests

I used the Zeebe Process Test and included dependency in pom.xml. This project allows you to unit test your Camunda Platform 8 BPMN processes. It will start a Zeebe test engine and provide you with a set of assertions you can use to verify your process behaves as expected.

This project provides different annotations and methods to apply automated tests for BPMN processes. Below are the examples of Tests for Deployment and Start process instance. You can refer to [AssignmentApplicationTests.java](https://github.com/ankitsrivastava/camundaapp/blob/master/src/test/java/com/ankit/camunda/work/assignment/AssignmentApplicationTests.java)

```
@Test
	public void testDeployment() {
		//When
		DeploymentEvent event = client.newDeployResourceCommand()
				.addResourceFromClasspath("random-animal.bpmn")
				.send()
				.join();

		//Then
		BpmnAssert.assertThat(event);
	}
```

```
@Test
	public void testProcessInstanceStart(){
		//Given
		initDeployment();

		//When
		ProcessInstanceEvent event = client.newCreateInstanceCommand()
				.bpmnProcessId("random-application")
				.latestVersion()
				.send()
				.join();

		//Then
		ProcessInstanceAssert assertions = BpmnAssert.assertThat(event);
		assertions.hasPassedElement("random-animal");
	}
```
# Containerize Client application

Create a Dockerfile in the root directory of your project. This file will contain the instructions to build the Docker image for your client application. Dockerfile content as below

```
# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the project’s build artifacts
COPY target/assignment-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
```
You will need to run ```mvn package``` to make sure the target jar is available.

## Build the Docker Image
Build the Docker image using the Dockerfile:
```
docker build -t camunda-client-app .
```
##  Run the Docker Container
Run the Docker container from the image you just built:
```
docker run -p 8080:8080 camunda-client-app
```
This command maps port 8080 of the Docker container to port 8080 on your host machine, allowing you to access your Spring Boot application at http://localhost:8080.

# Add a setup to make the app run locally on every machine

When you containerize your application, it can run locally on every machine that has an engine installed such as Docker. However there are a few property changes needed, remember clientID, clusterID, and secret defined in application.properties which need to be changed based on the host environment. If you need to build an app every time for such changes, it will be time-consuming. We should be able to provide provisions for such changes at the container level.

These properties can be overridden at runtime using environment variables, which Spring Boot automatically maps to properties. These environment variables can be changed at the container level and do not require re-building the application.

# A small architectural diagram that shows how these components interact with each other

![image](https://github.com/user-attachments/assets/5633d143-3e31-4248-b800-22b7414ad977)
