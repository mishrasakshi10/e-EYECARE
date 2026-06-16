// doctorApproval.js

document.addEventListener("DOMContentLoaded", loadPendingDoctors);

let doctorsData = [];

// ==========================
// Load pending doctors
// ==========================
function loadPendingDoctors() {
    fetch("/e-EYECARES/DoctorApprovalServlet")
        .then(res => res.json())
        .then(data => {
            console.log("Doctors Data:", data); // for debugging
            doctorsData = data;
            displayDoctors(data);
        })
        .catch(err => console.error("Error fetching doctors:", err));
}

// ==========================
// Display doctors in table
// ==========================
function displayDoctors(data) {
    let tableBody = document.querySelector("#doctorTable tbody");
    tableBody.innerHTML = "";

    data.forEach(doc => {
        let row = document.createElement("tr");

        row.innerHTML = `
            <td><img src="/e-EYECARES/uploads/${doc.profile_photo}" class="profile-img"></td>
            <td>${doc.name}</td>
            <td>${doc.specialization}</td>
            <td>${doc.experience} yrs</td>
            <td>₹${doc.consultation_fee}</td>
            <td>
                <button class="view" onclick="viewDetails(${doc.user_id})">View</button>
                <button class="approve" data-id="${doc.user_id}" onclick="updateStatus(${doc.user_id}, 'approved', this)">Approve</button>
                <button class="reject" data-id="${doc.user_id}" onclick="updateStatus(${doc.user_id}, 'rejected', this)">Reject</button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

// ==========================
// Search functionality
// ==========================
document.getElementById("searchInput").addEventListener("keyup", function () {
    let value = this.value.toLowerCase();
    let filtered = doctorsData.filter(doc =>
        doc.name.toLowerCase().includes(value) ||
        doc.specialization.toLowerCase().includes(value)
    );
    displayDoctors(filtered);
});

// ==========================
// Approve or Reject doctor
// ==========================
function updateStatus(id, status, btn) {
    fetch("/e-EYECARES/DoctorApprovalServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `user_id=${id}&status=${status}`
    })
    .then(res => res.text())
    .then(msg => {
        showToast(msg);

        // Replace buttons with a status badge
        const row = btn.closest("tr");
        if(row) {
            row.querySelector("td:last-child").innerHTML = 
                `<span class="status-badge ${status}">${status.charAt(0).toUpperCase() + status.slice(1)}</span>`;
        }
    })
    .catch(err => console.error("Error updating status:", err));
}

// ==========================
// Modal – View details
// ==========================
function viewDetails(id) {
    let doc = doctorsData.find(d => d.user_id === id);
    if (!doc) return;

    let modal = document.getElementById("doctorModal");
    let body = document.getElementById("modalBody");

    body.innerHTML = `
        <h3>${doc.name}</h3>
        <p><b>Email:</b> ${doc.email}</p>
        <p><b>Phone:</b> ${doc.phone}</p>
        <p><b>Gender:</b> ${doc.gender}</p>
        <p><b>Qualification:</b> ${doc.qualification}</p>
        <p><b>Registration No:</b> ${doc.registration_no}</p>
        <p><b>Experience:</b> ${doc.experience} yrs</p>
        <p><b>Consultation Fee:</b> ₹${doc.consultation_fee}</p>
    `;

    modal.style.display = "block";
}

function closeModal() {
    document.getElementById("doctorModal").style.display = "none";
}

// ==========================
// Toast notification
// ==========================
function showToast(message) {
    let toast = document.getElementById("toast");
    toast.innerText = message;
    toast.className = "show";
    setTimeout(() => toast.className = "", 3000);
}