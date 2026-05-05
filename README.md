# COSC 214 – Data Structures and Algorithms
## Project: Hospital Management System

## How to Run
1. Clone the repository from GitHub
2. Open the project in VS Code and run `HospitalSystem.java`

---

### Design Document – Part 1

| | |
|---|---|
| **Course** | COSC 214 – Data Structures and Algorithms |
| **Date** | April 19, 2026 |

**Team Members:**
- Ephram Mbapte
- Mohamed Kargbo
- Nia Allen
- Nicholas Armenta

---

## 1. Project Overview

**Title:** Hospital Management System (HMS)

**Objective:** The goal of this project is to manage hospital operations efficiently, including patient information, appointments, doctor schedules, and emergency prioritization.

**Description:** The system will allow hospital staff to manage patient records, schedule appointments, organize doctor availability, and prioritize emergency cases. The system supports real-time data access and efficient updates through the use of appropriate data structures for each task.

---

## 2. Data Model

Each patient in the system is represented as a `PatientRecord` object. A unique ID is generated automatically (sequentially starting at 1) during loading since the dataset does not provide one.

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Auto-generated unique identifier |
| `name` | `String` | Full name of the patient |
| `age` | `int` | Patient age |
| `gender` | `String` | Male or Female |
| `diagnosis` | `String` | Medical condition / diagnosis |
| `admissionType` | `String` | Emergency, Urgent, or Elective |
| `doctorId` | `int` | ID of the assigned doctor |
| `billingAmount` | `double` | Total billed amount |

Each doctor is represented as a `Doctor` object:

| Field | Type | Description |
|---|---|---|
| `doctorId` | `int` | Unique doctor identifier |
| `name` | `String` | Full name |
| `specialization` | `String` | Area of medical expertise |
| `availableHours` | `String` | Hours available for appointments |

**Java Class Definitions:**

```java
class PatientRecord {
    int id;
    String name;
    int age;
    String gender;
    String diagnosis;
    String admissionType;
    int doctorId;
    double billingAmount;
}

class Doctor {
    int doctorId;
    String name;
    String specialization;
    String availableHours;
}
```

---

## 3. Core Features and Data Structure Selection

### Feature 1 – Patient Record Management
**Description:** Store and retrieve complete patient details including name, age, diagnosis, and treatment history.

**Data Structure: Linked List**
A linked list is ideal here because patient records are frequently inserted and deleted as patients are admitted and discharged. Unlike arrays, linked lists do not require shifting elements on insertion or deletion, making them efficient for dynamic, frequently changing data.

| Operation | Time Complexity |
|---|---|
| Insert | O(1) (insert at head) |
| Search by ID | O(n) |
| Delete by ID | O(n) |
| Traverse | O(n) |

---

### Feature 2 – Appointment Scheduling
**Description:** Handle patient appointments in first-come-first-served order, with priority given to emergency cases.

**Data Structure: Queue (standard) + Heap / Priority Queue (emergency)**
A standard Queue enforces FIFO ordering for regular appointments. A Priority Queue (min-heap) is used alongside it to ensure emergency patients are always processed before urgent and elective cases regardless of arrival order.

**Priority Rules:**
| Admission Type | Priority Level |
|---|---|
| Emergency | 1 – Highest |
| Urgent | 2 – Medium |
| Elective | 3 – Lowest |

| Operation | Queue Time | Priority Queue Time |
|---|---|---|
| Enqueue | O(1) | O(log n) |
| Dequeue | O(1) | O(log n) |
| Traverse | O(n) | O(n) |

---

### Feature 3 – Doctor Management
**Description:** Store and retrieve doctor information (specialization, availability, assigned patients) using a unique doctor ID as the lookup key.

**Data Structure: Hash Table (HashMap)**
A HashMap provides near-constant time access by doctor ID, which is critical when quickly checking doctor availability during appointment scheduling. This is far more efficient than scanning a list.

| Operation | Time Complexity |
|---|---|
| Insert | O(1) average |
| Search by doctorId | O(1) average |
| Delete | O(1) average |
| Traverse | O(n) |

---

### Feature 4 – Emergency Patient Prioritization
**Description:** Ensure emergency patients are always handled before non-emergency patients, based on severity level.

**Data Structure: Heap (Priority Queue)**
A max-heap (or min-heap with priority values) re-orders patients on every insertion so the highest-severity patient is always at the front of the queue. This cannot be efficiently achieved with a standard array or linked list without re-sorting.

```
Priority comparator:
  Emergency  = 1  (highest — dequeued first)
  Urgent     = 2
  Elective   = 3  (lowest)
```

| Operation | Time Complexity |
|---|---|
| Insert (enqueue) | O(log n) |
| Get highest priority | O(1) |
| Remove highest priority | O(log n) |

---

### Feature 5 – Search Functionality
**Description:** Look up patients, doctors, or appointments by various parameters (ID, name, diagnosis).

**Data Structure: Hash Table for ID lookup / Linked List for sequential search**
- Searching by unique ID (patient or doctor): HashMap provides O(1) average lookup.
- Searching by name or diagnosis (non-unique fields): requires a linear scan through the linked list of patient records — O(n).

---

### Feature 6 – Treatment Log (Undo/Redo)
**Description:** Maintain a log of treatment actions performed on a patient so that staff can undo the most recent action if a mistake is made.

**Data Structure: Stack**
A stack (LIFO) is the natural fit for undo/redo functionality. Each treatment action is pushed onto the stack. Undoing an action pops the most recent entry. This is used in the patient record management module.

| Operation | Time Complexity |
|---|---|
| Push (log action) | O(1) |
| Pop (undo) | O(1) |
| Peek (view last action) | O(1) |

---

## 4. Data Structure Summary

| Data Structure | Feature Used In | Why It Was Chosen |
|---|---|---|
| Array | Static hospital department list | Fixed size, fast index access O(1) |
| Linked List | Patient record management | Dynamic insertion/deletion without shifting |
| Stack | Treatment log / undo | LIFO order matches undo behavior |
| Queue | Regular appointment scheduling | FIFO ensures fair order of service |
| Hash Table | Doctor management, ID search | O(1) average lookup by unique key |
| Heap / Priority Queue | Emergency prioritization | Always serves highest priority patient first |

---

## 5. System Architecture

```
+---------------------------------------------------------------+
|               Hospital Management System (HMS)                |
+---------------------------------------------------------------+
         |              |               |              |
         v              v               v              v
 [Patient Records] [Appointments]  [Doctor Mgmt]  [Treatment Log]
  Linked List       Queue +          HashMap         Stack
                    Priority Queue
         |              |               |              |
         +------+--------+--------------+--------------+
                |
                v
         [Search Module]
          HashMap (by ID)
          Linked List scan (by name/diagnosis)
                |
                v
         [Benchmarking Module]
          System.nanoTime() — insert / search / delete
          5 runs per operation, average reported
```

**Data Flow:**
1. CSV file is loaded and each row becomes a `PatientRecord` object
2. Records are inserted into the Linked List for general management
3. Records are also inserted into the HashMap keyed by ID for fast lookup
4. Admission queue routes patients — regular to Queue, emergency to Priority Queue
5. Treatment actions are pushed to the patient's Stack log
6. Doctor records are stored and accessed via HashMap

---

## 6. Pseudocode for Core Operations

### Patient Record – Linked List

```
insertRecord(head, record):
    newNode = Node(record)
    newNode.next = head
    head = newNode

searchRecord(head, id):
    current = head
    while current != null:
        if current.data.id == id:
            return current.data
        current = current.next
    return null

deleteRecord(head, id):
    if head.data.id == id:
        head = head.next
        return
    current = head
    while current.next != null:
        if current.next.data.id == id:
            current.next = current.next.next
            return
        current = current.next
```

### Doctor Management – HashMap

```
addDoctor(map, doctor):
    map.put(doctor.doctorId, doctor)

getDoctor(map, doctorId):
    return map.get(doctorId)

removeDoctor(map, doctorId):
    map.remove(doctorId)
```

### Appointment Queue

```
scheduleAppointment(queue, record):
    queue.enqueue(record)

processNext(queue):
    return queue.dequeue()
```

### Emergency Priority Queue

```
addEmergency(pq, record):
    pq.insert(record)   // heapified by admissionType priority

serveNext(pq):
    return pq.extractMin()  // always returns highest priority
```

### Treatment Log – Stack

```
logTreatment(stack, action):
    stack.push(action)

undoLastTreatment(stack):
    if stack is not empty:
        return stack.pop()
    return null
```

---

## 7. Team Responsibilities

| Team Member | Responsibility |
|---|---|
| Ephram Mbapte | Priority Queue + Heap (emergency prioritization) + benchmarking framework |
| Mohamed Kargbo | Linked List (patient record management) |
| Nia Allen | HashMap (doctor management + search) |
| Nicholas Armenta | Array + Queue + Stack implementations |

All members collaborate on the design document, performance analysis report, and final presentation.
