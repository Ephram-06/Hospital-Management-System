# COSC 214 – Data Structures and Algorithms
## Project: Hospital Management System

**Team Members:** Ephram Mbapte · Mohamed Kargbo · Nia Allen · Nicholas Armenta

---

## How to Run

```bash
# 1. Clone
git clone https://github.com/Ephram-06/Hospital-Management-System.git
cd Hospital-Management-System

# 2. Compile
javac -d out src/*.java

# 3. Run
java -cp out HospitalSystem
```

The system auto-loads **10,000 real patient records** from `data/patients.csv` (Kaggle healthcare dataset) and 4 doctors from `data/doctors.csv` on startup.

---

## Design Document

| | |
|---|---|
| **Course** | COSC 214 – Data Structures and Algorithms |
| **Date** | April 19, 2026 |

---

## 1. Project Overview

**Title:** Hospital Management System (HMS)

**Objective:** Build a fully functional CLI-based hospital management system that demonstrates the practical use of core data structures on a real-world dataset.

**Dataset:** [Kaggle Healthcare Dataset](https://www.kaggle.com/) — 10,000 patient records, 15 columns per row, loaded from `data/patients.csv` at startup.

---

## 2. Data Model

Each patient is represented as a `PatientRecord` object with 16 fields matching the Kaggle schema (ID is auto-generated on load):

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Auto-generated unique identifier (sequential from 1) |
| `name` | `String` | Full patient name |
| `age` | `int` | Patient age |
| `gender` | `String` | Male / Female |
| `bloodType` | `String` | Blood type (e.g. A+, O-) |
| `diagnosis` | `String` | Medical condition |
| `dateOfAdmission` | `String` | Admission date (MM/DD/YYYY) |
| `doctorName` | `String` | Assigned doctor's name |
| `hospital` | `String` | Hospital name |
| `insuranceProvider` | `String` | Insurance company |
| `billingAmount` | `double` | Total billed amount ($) |
| `roomNumber` | `int` | Assigned room |
| `admissionType` | `String` | Emergency / Urgent / Elective |
| `dischargeDate` | `String` | Discharge date (MM/DD/YYYY) |
| `medication` | `String` | Prescribed medication |
| `testResults` | `String` | Normal / Abnormal / Inconclusive |

Each doctor is represented as a `Doctor` object:

| Field | Type | Description |
|---|---|---|
| `name` | `String` | Full name |
| `specialization` | `String` | Area of medical expertise |
| `availableHours` | `String` | Availability schedule |

---

## 3. Menu Overview (15 Options)

When you run the program you get a menu with these options:

```
╔══════════════════════════════════════════════╗
║      Hospital Management System  (HMS)       ║
╠══════════════════════════════════════════════╣
║  PATIENT MANAGEMENT                          ║
║   1.  Add Patient                            ║
║   2.  List All Patients                      ║
║   3.  Search Patient                         ║
║   4.  Remove Patient                         ║
╠══════════════════════════════════════════════╣
║  DOCTOR MANAGEMENT                           ║
║   5.  View All Doctors                       ║
║   6.  Add Doctor                             ║
║   7.  Remove Doctor                          ║
╠══════════════════════════════════════════════╣
║  APPOINTMENTS                                ║
║   8.  Process Next Appointment               ║
║   9.  View Appointment Queues                ║
╠══════════════════════════════════════════════╣
║  TREATMENT LOG                               ║
║   10. Log Treatment Action                   ║
║   11. Undo Last Treatment                    ║
║   12. View Treatment Log                     ║
╠══════════════════════════════════════════════╣
║  REPORTS & TOOLS                             ║
║   13. Billing Statistics                     ║
║   14. Run Performance Benchmarks             ║
║   15. Sort & Browse Patients                 ║
║   0.  Exit                                   ║
╚══════════════════════════════════════════════╝
```

---

## 4. Core Features and Data Structure Selection

### Feature 1 – Patient Record Management (Linked List)

`PatientLinkedList.java` — custom singly linked list, no Java collections used internally.

Patients are inserted, searched, and deleted through the linked list. It was chosen because patient records are frequently added and removed — linked lists handle this without shifting elements like an array would.

| Operation | Time Complexity |
|---|---|
| Insert (at head) | O(1) |
| Search by ID / Name / Diagnosis / Blood Type / Hospital / Test Results | O(n) |
| Delete by ID | O(n) |
| Traverse | O(n) |

---

### Feature 2 – Doctor Management (HashMap)

`DoctorHashMap.java` — wraps `HashMap<String, Doctor>` keyed by doctor name.

A HashMap gives O(1) average lookup, which is critical when checking doctor assignments while processing thousands of patient records on load.

| Operation | Time Complexity |
|---|---|
| Add doctor | O(1) average |
| Look up by name | O(1) average |
| Remove | O(1) average |

---

### Feature 3 – Appointment Scheduling (Queue + Priority Queue)

`AppointmentQueue.java` — standard FIFO queue for Elective patients.  
`EmergencyPriorityQueue.java` — min-heap by admission priority for Emergency/Urgent patients.

When option 8 (Process Next Appointment) is chosen, the system always drains the emergency heap first before touching the regular queue.

| Admission Type | Priority |
|---|---|
| Emergency | 1 — served first |
| Urgent | 2 |
| Elective | 3 — served last |

| Operation | Queue | Priority Queue |
|---|---|---|
| Enqueue / Insert | O(1) | O(log n) |
| Dequeue / Extract | O(1) | O(log n) |

---

### Feature 4 – Treatment Log / Undo (Stack)

`TreatmentStack.java` — custom LIFO stack with timestamps.

Each logged treatment action is pushed onto the stack. Option 11 pops the top entry (undo). This naturally matches how undo works in any system.

| Operation | Time Complexity |
|---|---|
| Push (log) | O(1) |
| Pop (undo) | O(1) |

---

### Feature 5 – Search (6 Fields + Binary Search)

`SearchModule.java` — facade over the linked list and doctor map.

**Linear search** (O(n)) over the linked list for: Name, Diagnosis, Blood Type, Hospital, Test Results.  
**Binary search** (O(log n)) for Patient ID — `binarySearchById()` on a sorted list.

Results > 20 are automatically paginated so the terminal doesn't get flooded.

---

### Feature 6 – Sorting (Bubble / Merge / Quick Sort)

`SortingModule.java` — three sorting algorithms implemented from scratch.

All three sort by a chosen field (Name, Age, or Billing Amount). The original list is never modified — a new sorted list is returned. After sorting, results are displayed 20 per page with time taken shown in nanoseconds.

| Algorithm | Best | Average | Worst | Space |
|---|---|---|---|---|
| Bubble Sort | O(n) | O(n²) | O(n²) | O(1) |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) |

---

### Feature 7 – Billing Statistics

Option 13 prints a full billing report including:
- Total / average / highest / lowest billing
- Count of Emergency / Urgent / Elective admissions
- **Average billing broken down by test results** (Normal / Abnormal / Inconclusive)

---

### Feature 8 – Performance Benchmarks

`BenchmarkRunner.java` — benchmarks all data structures at sizes 100, 500, 1000, 5000, and 10000.

Runs each operation 5 times and reports the average in nanoseconds. Prints 6 separate tables:

1. Linked List — Insert, Search, Delete
2. Array (ArrayList) — Insert, Search, Delete
3. Hash Table (HashMap) — Insert, Lookup, Delete
4. Queue / Priority Queue — Enqueue, Dequeue, PQ Insert, PQ Extract
5. Sorting Algorithms — Bubble, Merge, Quick
6. Linear vs Binary Search comparison

Results are also saved to `data/benchmark_results.txt`.

---

## 5. Data Structure Summary

| Data Structure | Where Used | Why |
|---|---|---|
| Singly Linked List | Patient records | O(1) insert, dynamic size |
| HashMap | Doctor management, fast ID lookup | O(1) average lookup |
| Stack | Treatment log / undo | LIFO matches undo behavior |
| Queue (FIFO) | Regular appointment scheduling | Fair first-come-first-served order |
| Min-Heap (Priority Queue) | Emergency prioritization | Highest severity always served first |
| ArrayList | Bulk operations, sorting, benchmarks | Random access O(1), sort-friendly |

---

## 6. System Architecture

```
+------------------------------------------------------------------+
|                Hospital Management System (HMS)                  |
+------------------------------------------------------------------+
      |              |               |              |          |
      v              v               v              v          v
[Patient LL]  [Appt Queue]    [Doctor Map]   [Treat. Stack] [Sorting]
 Linked List   Queue +          HashMap         Stack       Bubble /
               Min-Heap                                     Merge /
                                                            Quick
      |              |               |
      +------+--------+--------------+
             |
             v
      [Search Module]
       Binary Search (by ID, sorted list)
       Linear Scan  (by name / diagnosis / blood type / hospital / test results)
             |
             v
      [Benchmark Runner]
       Timed tests at 5 dataset sizes
       6 result tables → printed + saved to file
```

---

## 7. Pseudocode for Core Operations

### Linked List – Insert / Search / Delete

```
insert(record):
    newNode = Node(record)
    newNode.next = head
    head = newNode

searchById(id):
    current = head
    while current != null:
        if current.data.id == id → return current.data
        current = current.next
    return null

deleteById(id):
    if head.data.id == id → head = head.next; return
    current = head
    while current.next != null:
        if current.next.data.id == id:
            current.next = current.next.next; return
        current = current.next
```

### HashMap – Doctor Lookup

```
addDoctor(doctor):     map.put(doctor.name, doctor)
getDoctor(name):       return map.get(name)
removeDoctor(name):    map.remove(name)
```

### Priority Queue – Emergency Scheduling

```
insert(patient):
    heap.add(patient)
    siftUp(heap.size - 1)      // O(log n)

extractMin():
    min = heap[0]
    heap[0] = heap[last]
    heap.removeLast()
    siftDown(0)                // O(log n)
    return min
```

### Merge Sort

```
mergeSort(list, field):
    if list.size <= 1 → return list
    mid = list.size / 2
    left  = mergeSort(list[0..mid],   field)
    right = mergeSort(list[mid..end], field)
    return merge(left, right, field)

merge(left, right, field):
    result = []
    while left and right not empty:
        if compare(left[0], right[0], field) <= 0:
            result.add(left.removeFirst())
        else:
            result.add(right.removeFirst())
    result.addAll(remaining)
    return result
```

### Binary Search (by ID)

```
binarySearchById(sortedList, targetId):
    lo = 0, hi = sortedList.size - 1
    while lo <= hi:
        mid = (lo + hi) / 2
        if sortedList[mid].id == targetId → return sortedList[mid]
        if sortedList[mid].id < targetId  → lo = mid + 1
        else                              → hi = mid - 1
    return null
```

---

## 8. Team Responsibilities

| Team Member | Primary Responsibility |
|---|---|
| Ephram Mbapte | Priority Queue, benchmarking framework, sorting module, search extensions |
| Mohamed Kargbo | Linked List (patient records) |
| Nia Allen | HashMap (doctor management + search module) |
| Nicholas Armenta | Array, Queue, Stack implementations |


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
