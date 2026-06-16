document.addEventListener("DOMContentLoaded", () => {
    loadPrescriptions();
});

function loadPrescriptions(search = "") {

    fetch("/e-EYECARES/ManagePrescriptionsServlet?search=" + encodeURIComponent(search))
        .then(res => res.json())
        .then(data => {

            let table = document.getElementById("prescriptionTable");
            table.innerHTML = "";

            if (data.length === 0) {
                table.innerHTML =
                    "<tr><td colspan='8'>No Prescriptions Found</td></tr>";
                return;
            }

            data.forEach(p => {

                let row = `
                    <tr>
                        <td>${p.prescription_id}</td>
                        <td>${p.patient_name}</td>
                        <td>${p.doctor_name}</td>
                        <td>${p.diagnosis}</td>
                        <td>${p.medicines}</td>
                        <td>${p.notes}</td>
                        <td>${p.created_at}</td>
                        <td>
                            <button class="delete"
                                onclick="deletePrescription(${p.prescription_id})">
                                Delete
                            </button>
                        </td>
                    </tr>
                `;

                table.innerHTML += row;
            });
        })
        .catch(err => console.error(err));
}

function deletePrescription(id) {

    if (!confirm("Delete this prescription?")) return;

    fetch("/e-EYECARES/ManagePrescriptionsServlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "action=delete&prescription_id=" + id
    })
    .then(res => res.text())
    .then(msg => {
        alert(msg);
        loadPrescriptions();
    });
}

function searchPrescriptions() {
    let value = document.getElementById("searchInput").value;
    loadPrescriptions(value);
}