# Task 2 – Hospital Outpatient Appointment Desk

## 2.1 Event model

| Source | Event | Listener | Action |
|---|---|---|---|
| Department JComboBox | ItemEvent | ItemListener | Load matching doctors from Map and refresh doctor combo |
| Doctor JComboBox | ItemEvent | ItemListener | Refresh available time slots |
| Book JButton | ActionEvent | ActionListener | Validate fields and book appointment |
| Window | WindowEvent | WindowListener/WindowAdapter | Ask confirmation if booking is in progress |
| Cancel JButton | ActionEvent | ActionListener | Clear form |

## 2.2 Component hierarchy
```
JFrame AppointmentFrame (BorderLayout)
 ├─ JPanel form (GridBagLayout)
 │   ├─ JLabel + JTextField patientId
 │   ├─ JLabel + JComboBox department
 │   ├─ JLabel + JComboBox doctor
 │   ├─ JLabel + JComboBox timeSlot
 │   └─ JButton Book, JButton Clear
 └─ JLabel status (SOUTH)
```

## 2.3 Handler pseudocode
**Department changed**
```
ON department selection change
    selectedDepartment ← departmentCombo value
    doctorCombo remove all items
    FOR EACH doctor IN doctorMap[selectedDepartment]
        doctorCombo add doctor
    END FOR
    refresh time slots
END
```

**Validate and book**
```
ON Book click
    IF patient ID is empty THEN show error; RETURN
    IF department, doctor or slot is not selected THEN show error; RETURN
    mark bookingInProgress ← true
    disable Book button
    RUN booking service in SwingWorker
        verify slot availability and save appointment
    ON completion
        mark bookingInProgress ← false
        enable Book button
        show confirmation or error
END
```

## 2.4 Event trace and applet comparison
Create frame → construct components → register listeners → pack → setVisible → user selects department → ItemListener runs → doctor list refreshes → user clicks Book → ActionListener validates → background service saves → completion callback updates status → user closes → windowClosing asks confirmation → dispose.

Applet methods init(), start(), paint(), stop(), destroy() are container-controlled lifecycle callbacks. A desktop JFrame is created by application code; its events are dispatched to registered listeners and it is closed/disposed by the application.

## Alternative and complexity
Loading all doctors once into Map<String,List<Doctor>> costs O(d+m) setup time and O(d+m) memory; a department change is O(m_d) to populate that department's list. Fetching from the database each time costs network/query latency plus O(m_d) result processing per change, but needs less retained memory (roughly O(m_d) transient). Caching is faster for repeated changes; database fetching is useful when doctor data changes frequently.
