document.addEventListener("DOMContentLoaded", () => {
    loadAppointments();
});

function loadAppointments(search = "") {

    fetch("/e-EYECARES/ManageAppointmentsServlet?search=" + encodeURIComponent(search))
        .then(res => res.json())
        .then(data => {

            console.log("Received Data:", data); // Debug

            let table = document.getElementById("appointmentTable");
            table.innerHTML = "";

            if (data.length === 0) {
                table.innerHTML = `
                    <tr>
                        <td colspan="7">No Appointments Found</td>
                    </tr>
                `;
                return;
            }

            data.forEach(app => {

                let row = `
                    <tr>
                        <td>${app.id}</td>
                        <td>${app.patient_name || "-"}</td>
                        <td>${app.doctor_name || "-"}</td>
                        <td>${app.appointment_date}</td>
                        <td>${app.appointment_time}</td>
                        <td>${app.problem}</td>
                        <td class="status-${app.status}">
                            ${app.status}
                        </td>
                        <td>
                            <button class="approve"
                                onclick="updateStatus(${app.id}, 'approved')">
                                Approve
                            </button>

                            <button class="reject"
                                onclick="updateStatus(${app.id}, 'rejected')">
                                Reject
                            </button>

                            <button class="delete"
                                onclick="deleteAppointment(${app.id})">
                                Delete
                            </button>
                        </td>
                    </tr>
                `;

                table.innerHTML += row;
            });
        })
        .catch(error => {
            console.error("Error loading appointments:", error);
        });
}


function updateStatus(id, status) {

   fetch("/e-EYECARES/ManageAppointmentsServlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "action=update&id=" + id + "&status=" + status
    })
    .then(res => res.text())
    .then(msg => {
        alert(msg);
        loadAppointments();
    })
    .catch(error => {
        console.error("Error updating status:", error);
    });
}


function deleteAppointment(id) {

    if (!confirm("Delete this appointment?")) return;

    fetch("/e-EYECARES/ManageAppointmentsServlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "action=delete&id=" + id
    })
    .then(res => res.text())
    .then(msg => {
        alert(msg);
        loadAppointments();
    })
    .catch(error => {
        console.error("Error deleting appointment:", error);
    });
}


function searchAppointments() {
    let value = document.getElementById("searchInput").value;
    loadAppointments(value);
}