# Simple implementation of a hospital system using JOptionPane

### **Hospital Management System**
I developed a **Health Information System** with two main functionalities:
1. **User Management**: Managing users such as patients, physicians, and nurses.
2. **Treatment Management**: Handling patient admission to discharge.

## **User Categories**
The system users are divided into four categories:
1. **Admin**
2. **Physician**
3. **Nurse**
4. **Patient**

All users must log in, and a customized menu will be displayed based on the user type.

## **Login Process**
- Users log in with a **username** and **password**.
- If the username does not exist, an **error message** is displayed.
- After **three failed attempts**, the account is **locked for 2 minutes**.
- If the username and password are both **"admin"**, the **admin menu** is displayed.

---

## **Admin Menu**
### 1. User Management
- **List All Users** → Show all users with name and last name.
- **Search User** → Search by last name (e.g., input "ra" returns Rahmani, Mehrabi, etc.).
- **Add User** → 
  - Add **Physician** (name, last name, field, record, sex).
  - Add **Nurse** (name, last name, record, sex).
  - Add **Patient** (name, last name, age, sex, disease, mode [VIP/Normal/Insurance]).
  - Each user has a **unique three-digit ID** (xxx).
- **Delete User** → Delete a user by their ID.

### 2. Change Password
- Change the admin password (**must contain** at least one special character `!@#$%&*`).

### 3. Exit
- Ends the program.

---

## **Physician Menu**
1. **Pick Patient**
2. **List All Patients**
3. **View Patient Info**
4. **Write Medicine**
5. **Discharge Patient**
6. **Change Password** (same rules as admin).
7. **Exit** (Back to the first menu).

**Config File (`config.txt`)**:
- This file defines which medical specialties can treat specific diseases.
- Example:
  ```
  Heart: heart attack, blood pressure
  Orthopedic: knee ache, elbow ache
  ```
- Customize the fields to make them testable.

### **Physician Operations**
- **Pick Patient** → Show patients that have not been assigned and match the physician's specialty.
- **List Patients** → Display all patients with name, last name, problem, and admission date.
- **View Patient Info** → Search patients by name, last name, or ID.
- **Write Medicine** → Respond to nurse messages by prescribing medicine.
- **Discharge Patient** → Release a patient and archive their information.

---

## **Nurse Menu**
1. **Check Patient State**
2. **Change Password** (same rules as admin).
3. **Exit** (Back to menu).

### **Nurse Operations**
- **Check Patient State** (multiple options):
  - **No Doctor Assigned** → List patients without an assigned doctor.
  - **Checked In** → Search patients admitted within a specified date range.
  - **Get Prescription** → Receive a message from doctors and confirm medicine administration.
  - **Discharge** → List patients with doctor-approved discharge.

---

## **Patient Menu**
1. **Check Out**
2. **Change Password**
3. **Exit**

### **Check Out Process**
- If the doctor discharges a patient, calculate the **total hospital stay** and charge accordingly:
  - **VIP** → $120 per day
  - **Normal** → $70 per day
  - **Insurance** → $35 per day

---

## **Programming Specifications**
- **Used OOP principles**.
- **Clean code**
- **Java UI** (Usig JOptionPane).
- **Database** Stored in different files.

---

