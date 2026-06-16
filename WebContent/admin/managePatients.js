document.addEventListener("DOMContentLoaded", () => {
    loadPatients();
});

function loadPatients(search = "") {

    fetch("/e-EYECARES/ManagePatientsServlet?search=" + encodeURIComponent(search))
        .then(res => res.json())
        .then(data => {

            const container = document.getElementById("patientContainer");
            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = "<p>No patients found.</p>";
                return;
            }

            data.forEach(p => {

                const statusClass = p.status === "active"
                    ? "status-active"
                    : "status-inactive";

                const card = document.createElement("div");
                card.className = "patient-card";

                card.innerHTML = `
                    <h3>${p.name}</h3>
                    <p><strong>Email:</strong> ${p.email}</p>
                    <p><strong>Age:</strong> ${p.age}</p>
                    <p><strong>Gender:</strong> ${p.gender}</p>
                    <p><strong>Phone:</strong> ${p.phone}</p>
                    <p class="${statusClass}">
                        ${p.status}
                    </p>

                    <div class="card-buttons">
                        <button class="view-btn">View</button>
                        <button class="toggle-btn">Toggle Status</button>
                    </div>
                `;

                card.querySelector(".view-btn")
                    .addEventListener("click", () => viewPatient(p));

                card.querySelector(".toggle-btn")
                    .addEventListener("click", () => toggleStatus(p.user_id));

                container.appendChild(card);
            });
        });
}

function searchPatient() {
    const value = document.getElementById("searchInput").value.trim();
    loadPatients(value);
}

function viewPatient(p) {

    document.getElementById("patientDetails").innerHTML = `
        <h3>${p.name}</h3>
        <p><strong>Email:</strong> ${p.email}</p>
        <p><strong>Age:</strong> ${p.age}</p>
        <p><strong>Gender:</strong> ${p.gender}</p>
        <p><strong>Phone:</strong> ${p.phone}</p>
        <p><strong>Status:</strong> ${p.status}</p>
    `;

    document.getElementById("viewModal").style.display = "block";
}

function closeModal() {
    document.getElementById("viewModal").style.display = "none";
}

function toggleStatus(id) {
    fetch("/e-EYECARES/TogglePatientStatusServlet?id=" + id)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert("Status changed to " + data.newStatus);
                loadPatients(); // reload updated list
            } else {
                alert("Failed to change status: " + data.message);
            }
        })
        .catch(err => {
            console.error(err);
            alert("Error toggling status.");
        });
}