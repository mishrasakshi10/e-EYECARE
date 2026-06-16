// Load doctors when page loads
document.addEventListener("DOMContentLoaded", () => {
    loadDoctors();
});


// ===============================
// LOAD DOCTORS
// ===============================
function loadDoctors(search = "") {

    fetch("/e-EYECARES/ManageDoctorsServlet?search=" + encodeURIComponent(search))
        .then(response => {

            if (!response.ok) {
                throw new Error("Failed to load doctors");
            }

            return response.json();
        })
        .then(data => {

            const container = document.getElementById("doctorContainer");
            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = "<p>No doctors found.</p>";
                return;
            }

            data.forEach(doc => {

                const statusClass = doc.status === "active"
                    ? "status-active"
                    : "status-inactive";

                const card = document.createElement("div");
                card.className = "doctor-card";

                card.innerHTML = `
                    <h3>${doc.name}</h3>
                    <p><strong>Specialization:</strong> ${doc.specialization}</p>
                    <p><strong>Experience:</strong> ${doc.experience} years</p>
                    <p><strong>Consultation Fee:</strong> ₹${doc.fees}</p>
                    <p class="${statusClass}">
                        ${doc.status}
                    </p>

                    <div class="card-buttons">
                        <button class="view-btn">View</button>
                        <button class="toggle-btn">Toggle Status</button>
                    </div>
                `;

                // Attach button events safely
                card.querySelector(".view-btn")
                    .addEventListener("click", () => viewDoctor(doc));

                card.querySelector(".toggle-btn")
                    .addEventListener("click", () => toggleStatus(doc.user_id));

                container.appendChild(card);
            });

        })
        .catch(error => {
            console.error("Error:", error);
            document.getElementById("doctorContainer").innerHTML =
                "<p>Error loading doctors.</p>";
        });
}


// ===============================
// SEARCH FUNCTION
// ===============================
function searchDoctor() {
    const value = document.getElementById("searchInput").value.trim();
    loadDoctors(value);
}


// ===============================
// VIEW DOCTOR MODAL
// ===============================
function viewDoctor(doc) {

    const modal = document.getElementById("viewModal");
    const details = document.getElementById("doctorDetails");

    details.innerHTML = `
        <h3>${doc.name}</h3>
        <p><strong>Email:</strong> ${doc.email}</p>
        <p><strong>Specialization:</strong> ${doc.specialization}</p>
        <p><strong>Experience:</strong> ${doc.experience} years</p>
        <p><strong>Consultation Fee:</strong> ₹${doc.fees}</p>
        <p><strong>Status:</strong> ${doc.status}</p>
    `;

    modal.style.display = "block";
}


// ===============================
// CLOSE MODAL
// ===============================
function closeModal() {
    document.getElementById("viewModal").style.display = "none";
}


// ===============================
// TOGGLE DOCTOR STATUS
// ===============================
function toggleStatus(id) {

    fetch("/e-EYECARES/ToggleDoctorStatusServlet?id=" + id)
        .then(response => {

            if (!response.ok) {
                throw new Error("Failed to update status");
            }

            // Reload doctors after toggle
            loadDoctors();
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Unable to update doctor status.");
        });
}