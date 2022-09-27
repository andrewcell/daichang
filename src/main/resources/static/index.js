const counts = JSON.parse(document.getElementById("equipmentCounts").innerText);

const pcCounts = counts[0];
const laptopCounts = counts[1];
const monitorCounts = counts[2];

const config = (index) => {
    return {
        type: 'pie',
        data: {
            labels: [
                "사용가능",
                "사용불가",
                "폐기예정"
            ],
            datasets: [{
                data: counts[index],
                backgroundColor: [
                    'rgb(255, 99, 132)',
                    'rgb(54, 162, 235)',
                    'rgb(255, 205, 86)'
                ],
            }]
        }
    }
}

const pcChart = new Chart(
    document.getElementById("pcChart"),
    config(0)
)
const laptopChart = new Chart(
    document.getElementById("laptopChart"),
    config(1)
)
const monitorChart = new Chart(
    document.getElementById("monitorChart"),
    config(2)
)