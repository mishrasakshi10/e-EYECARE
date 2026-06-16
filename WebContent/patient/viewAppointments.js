function loadAppointments() {

    fetch("../ViewAppointmentsJSONServlet")
    .then(response => response.json())
    .then(data => {

        let table = document.getElementById("apptBody");

        table.innerHTML = "";

        if (data.error) {

            table.innerHTML =
            "<tr><td colspan='5'>Please login first</td></tr>";

            return;
        }

        if (data.length === 0) {

            table.innerHTML =
            "<tr><td colspan='5'>No Appointments Found</td></tr>";

            return;
        }

        data.forEach(a => {

            let row = `
            <tr>
                <td>${a.doctor}</td>
                <td>${a.date}</td>
                <td>${a.time}</td>
                <td>${a.problem}</td>
                <td>${a.status}</td>
            </tr>
            `;

            table.innerHTML += row;
        });

    })
    .catch(error => {
        console.log("Error:", error);
    });
}

window.onload = loadAppointments;
