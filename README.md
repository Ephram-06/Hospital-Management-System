# COSC 214 – Data Structures and Algorithms
## Project: Hospital Management System

**Team Members:** Ephram Mbapte · Mohamed Kargbo · Nia Allen · Nicholas Armenta  
**Course:** COSC 214 – Data Structures and Algorithms  
**Date:** May 5, 2026

---

## How to Run

1. Clone the repository from GitHub
2. Open the project in VS Code and run `HospitalSystem.java`


The system auto-loads **~55,500 real patient records** from `data/patients.csv` (Kaggle healthcare dataset) and doctors from `data/doctors.csv` on startup.

---

## 1. Project Overview

**Title:** Hospital Management System (HMS)

**Objective:** Build a fully functional CLI-based hospital management system that demonstrates the practical use of core data structures on a real-world, large-scale dataset.

**Dataset:** [Kaggle Healthcare Dataset](https://www.kaggle.com/) — ~55,500 patient records, 15 columns per row, loaded from `data/patients.csv` at startup.

On launch, an ASCII robot animation boots the system with a live progress display, then walks across the terminal painting the welcome message letter by letter.

---

## 2. Data Model

Each patient is represented as a `PatientRecord` object with 16 fields:

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
| `roomNumber` | `int` | Assigned room number |
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

## 3. Menu Overview (16 Options)

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
║   16. Live Queue Monitor                     ║
║   0.  Exit                                   ║
╚══════════════════════════════════════════════╝
```

---

## 4. Core Features and Data Structure Selection

### Feature 1 – Patient Record Management (Linked List)

`PatientLinkedList.java` — custom singly linked list, no Java collections used internally.

Patients are inserted, searched, and deleted through the linked list. Chosen because patient records are frequently added and removed — linked lists handle this without shifting elements like an array would.

| Operation | Time Complexity |
|---|---|
| Insert (at head) | O(1) |
| Search by ID / Name / Diagnosis / Blood Type / Hospital / Test Results | O(n) |
| Delete by ID | O(n) |
| Traverse | O(n) |

---

### Feature 2 – Doctor Management (HashMap)

`DoctorHashMap.java` — wraps `HashMap<String, Doctor>` keyed by doctor name.

A HashMap gives O(1) average lookup, critical when checking doctor assignments while processing thousands of patient records on load.

| Operation | Time Complexity |
|---|---|
| Add doctor | O(1) average |
| Look up by name | O(1) average |
| Remove | O(1) average |

---

### Feature 3 – Appointment Scheduling (Queue + Priority Queue)

`AppointmentQueue.java` — standard FIFO queue for Elective patients.  
`EmergencyPriorityQueue.java` — min-heap by admission priority for Emergency/Urgent patients.

Option 8 (Process Next Appointment) always drains the emergency heap first. The robot animation (`RobotAnimation.java`) dispatches an animated robot to "fetch" the patient — emergency mode triggers a red flashing `!!! EMERGENCY DISPATCH !!!` header and the robot zooms at double speed.

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

**Linear search** O(n) over the linked list for: Name, Diagnosis, Blood Type, Hospital, Test Results.  
**Binary search** O(log n) for Patient ID — `binarySearchById()` on a sorted list.

Results > 20 are automatically paginated. Searching by ID also displays a full color-coded patient card with all 16 fields.

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
- Average billing broken down by test results (Normal / Abnormal / Inconclusive)

---

### Feature 8 – Performance Benchmarks

`BenchmarkRunner.java` — benchmarks all data structures at sizes 100, 500, 1000, 5000, and 10,000.

Runs each operation 5 times and reports the average in nanoseconds. Prints 6 separate tables:

1. Linked List — Insert, Search, Delete
2. Array (ArrayList) — Insert, Search, Delete
3. Hash Table (HashMap) — Insert, Lookup, Delete
4. Queue / Priority Queue — Enqueue, Dequeue, PQ Insert, PQ Extract
5. Sorting Algorithms — Bubble, Merge, Quick
6. Linear vs Binary Search comparison

Results are optionally saved to `data/benchmark_results.txt`.

---

### Feature 9 – Live Queue Monitor (Option 16)

Real-time dashboard that refreshes every 2 seconds showing live counts of:
- Regular appointment queue size
- Emergency priority queue size
- Treatment stack depth

Press Enter at any time to exit back to the menu.

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
|                    RobotAnimation (startup)                      |
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
       Linear Scan  (name / diagnosis / blood type / hospital / test results)
             |
             v
      [Benchmark Runner]
       Timed tests at 5 dataset sizes
       6 result tables → printed + optionally saved to file
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
| Ephram Mbapte | Priority Queue, benchmarking framework, sorting module, search extensions, robot animation |
| Mohamed Kargbo | Linked List (patient records) |
| Nia Allen | HashMap (doctor management + search module) |
| Nicholas Armenta | Array, Queue, Stack implementations |


---
