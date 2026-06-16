function loadAppointments() {
    fetch("/e-EYECARES/ViewDoctorAppointmentsServlet")
    .then(response => response.json())
    .then(data => {
        let table = document.getElementById("apptBody");
        table.innerHTML = "";

        if (data.error) {
            table.innerHTML = "<tr><td colspan='7'>Please login as Doctor first</td></tr>";
            return;
        }

        if (data.length === 0) {
            table.innerHTML = "<tr><td colspan='7'>No Appointments Found</td></tr>";
            return;
        }

        let rows = '';
        data.forEach(a => {
            let statusClass = "";
            if (a.status?.toLowerCase() === "approved") statusClass = "status-approved";
            else if (a.status?.toLowerCase() === "pending") statusClass = "status-pending";
            else if (a.status?.toLowerCase() === "rejected") statusClass = "status-rejected";

            let actionButtons = '';
            if (a.status?.toLowerCase() === "pending") {
                actionButtons = `
                    <button class="accept" onclick="updateStatus(${a.id}, 'approved')">Accept</button>
                    <button class="reject" onclick="updateStatus(${a.id}, 'rejected')">Reject</button>
                `;
            }

            rows += `
                <tr>
                    <td>${a.id}</td> <!-- ADD THIS -->
                    <td>${a.patient}</td>
                    <td>${a.date}</td>
                    <td>${a.time}</td>
                    <td>${a.problem}</td>
                    <td class="${statusClass}">${a.status}</td>
                    <td>${actionButtons}</td>
                </tr>
            `;
        });

        table.innerHTML = rows;
    })
    .catch(error => console.log("Error:", error));
}

function updateStatus(appointmentId, status) {
    fetch("/e-EYECARES/UpdateAppointmentsStatusServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `id=${appointmentId}&status=${status}`
    })
    .then(response => response.text())
    .then(result => {
        console.log(result);
        loadAppointments(); // Reload table after update
    })
    .catch(error => console.log("Error:", error));
}

window.onload = loadAppointments;
