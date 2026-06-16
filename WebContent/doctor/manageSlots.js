document.addEventListener("DOMContentLoaded", function () {
    loadSlots();
});

// =======================
// LOAD SLOTS FROM DATABASE
// =======================
function loadSlots() {

    fetch("../viewSlots")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch slots");
            }
            return response.json();
        })
        .then(data => {
            let list = document.getElementById("slotList");
            list.innerHTML = "";

            if (data.length === 0) {
                list.innerHTML = "<li>No slots added yet</li>";
                return;
            }

            data.forEach(slot => {

                let li = document.createElement("li");

                li.innerHTML = `
                    <span>
                        ${slot.date} | ${slot.time} 
                        <strong>(${slot.status})</strong>
                    </span>
                    <button onclick="deleteSlot(${slot.id})" class="delete-btn">
                        Delete
                    </button>
                `;

                list.appendChild(li);
            });
        })
        .catch(error => {
            console.error("Error loading slots:", error);
        });
}

function goBack() {
    window.location.href = "/e-EYECARES/doctor/dashboard.html"; // replace with your actual page
}


// =======================
// ADD SLOT
// =======================
function addSlot() {

   
    let date = document.getElementById("slotDate").value;
    let time = document.getElementById("slotTime").value;

    if (date === "" || time === "") {
        alert("Please select date and time");
        return;
    }

    fetch("../addSlots", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "slotDate=" + date + "&slotTime=" + time
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to add slot");
            }
            return response.text();
        })
        .then(() => {
            document.getElementById("slotDate").value = "";
            document.getElementById("slotTime").value = "";
            loadSlots();   // reload slots
        })
        .catch(error => {
            console.error("Error adding slot:", error);
        });
}


// =======================
// DELETE SLOT
// =======================
function deleteSlot(id) {

    if (!confirm("Are you sure you want to delete this slot?")) {
        return;
    }

    fetch("../deleteSlots?id=" + id)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to delete slot");
            }
            loadSlots();   // reload after delete
        })
        .catch(error => {
            console.error("Error deleting slot:", error);
        });
}
