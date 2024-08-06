# Fetch Animal Picture Process Model

This project demonstrates how to create a BPMN process model using Camunda BPMN Modeler that fetches a picture based on a user’s selection (cat, dog, or bear). I used Camunda SaaS for this exercise.

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

### BPMN XML

You can find the generated BPMN XML [here](https://github.com/ankitsrivastava/camundaapp/blob/master/src/main/resources/random-animal.bpmn).
