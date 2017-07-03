const contentRow = $("#contentRow");

const updateData = (e) => {
    const status = JSON.parse(e.data);

    contentRow.find("#corpusCount").html(status.corpuses.length);
    contentRow.find("#electronicsCount").html(status.electronics.length);
    contentRow.find("#wheelsCount").html(status.wheels.length);
    contentRow.find("#carsCount").html(status.cars.length);
};

const statusUpdate = new EventSource("api/socket");
statusUpdate.addEventListener("message", updateData, false);

$("#produceCar").on("click", (e) => {
   $.ajax({
        url: "api/produce/car",
        method: "GET",
        success: (d) => {
        }
   })
});

