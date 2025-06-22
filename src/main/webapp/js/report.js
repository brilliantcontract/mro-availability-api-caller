            async function generateReport() {
                $("#report-generation-form").addClass("d-none");
                $("#generated-report").removeClass("d-none");
                $("body").removeClass("container");
                $("body").removeClass("mt-3");
                $("body").addClass("m-3");

                /* ---------- 1. read cookies ---------- */
                const cookiesText = document.getElementById("cookies").value.trim();
                if (!cookiesText) {
                    alert("Please insert cookies with mrosupply.com session ID.");
                    return;
                }

                /* ---------- 2. read & parse products JSON from textarea ---------- */
                const listingsText = document.getElementById("productsToCheck").value.trim();
                let listings = [];
                try {
                    listings = JSON.parse(listingsText);
                } catch (e) {
                    alert("Products JSON is not valid. Please fix the content of the Products textarea.");
                    return;
                }
                if (!Array.isArray(listings) || listings.length === 0) {
                    alert("Products list is empty.");
                    return;
                }

                /* ---------- 3. prepare UI ---------- */
                const processingDiv = document.getElementById("processing");
                const doneCounter = document.getElementById("done");
                const lengthCounter = document.getElementById("length");
                const resultsTable = document.getElementById("results");
                const resultsBody = document.getElementById("results-body");

                resultsBody.innerHTML = "";
                doneCounter.innerText = "0";
                lengthCounter.innerText = listings.length;
                resultsTable.classList.add("d-none");   // hidden until we have rows
                processingDiv.classList.remove("d-none");

                /* ---------- 4. run checks ---------- */
                let counter = 0;
                for (const listing of listings) {
                    counter++;
                    doneCounter.innerText = counter;

                    const {supplier, id1, id2, id3} = listing;

                    const result1 = await checkLoggedInProduct(id1, cookiesText);

                    const result2 = await checkLoggedInProduct(id2, cookiesText);

                    const result3 = await checkLoggedInProduct(id3, cookiesText);

                    const result4 = await checkLoggedOutProduct(id1, cookiesText);

                    const result5 = await checkLoggedOutProduct(id2, cookiesText);

                    const result6 = await checkLoggedOutProduct(id3, cookiesText);

                    const result7 = await checkAdminProduct(id1, cookiesText);

                    const result8 = await checkAdminProduct(id2, cookiesText);

                    const result9 = await checkAdminProduct(id3, cookiesText);

                    displayResult({
                        supplier,
                        id1,
                        id2,
                        id3,
                        result1,
                        result2,
                        result3,
                        result4,
                        result5,
                        result6,
                        result7,
                        result8,
                        result9
                    });

                    $(function () {
                        $('[data-toggle="tooltip"]').tooltip({container: 'body'});
                    })
                }

                /* ---------- 5. finish ---------- */
                $("#report-generation-form").addClass("d-none");
                processingDiv.classList.add("d-none");
            }

            async function checkLoggedInProduct(productId, cookiesText) {
                const url = "/mro-availability-api-caller/resources/product-availability-and-price-on-front-end";

                const body = new URLSearchParams({
                    productId: productId.toString(),
                    cookies: cookiesText,
                });

                let attempts = 0;
                const maxAttempts = 5;

                while (attempts < maxAttempts) {
                    try {
                        const response = await fetch(url, {
                            method: "POST",
                            headers: {"Content-Type": "application/x-www-form-urlencoded"},
                            body: body.toString(),
                        });

                        if (response.status === 403) {
                            const data = await response.json();
                            return {status: "Fail", data: data};
                        }

                        if (!response.ok) {
                            throw new Error("HTTP error " + response.status);
                        }

                        const data = await response.json();
                        if (data && data.status !== "processing") {
                            return {status: "Success", data};
                        }
                    } catch (err) {
                        console.warn("Error or still processing, retrying...", err);
                    }

                    attempts++;
                    await new Promise((resolve) => setTimeout(resolve, 2000));
                }

                return {status: "Unsuccessful", data: null};
            }

            async function checkLoggedOutProduct(productId, cookiesText) {
                const url = "/mro-availability-api-caller/resources/product-availability-and-price-on-front-end-logged-out";

                const body = new URLSearchParams({
                    productId: productId.toString(),
                    cookies: cookiesText,
                });

                let attempts = 0;
                const maxAttempts = 5;

                while (attempts < maxAttempts) {
                    try {
                        const response = await fetch(url, {
                            method: "POST",
                            headers: {"Content-Type": "application/x-www-form-urlencoded"},
                            body: body.toString(),
                        });

                        if (response.status === 403) {
                            const data = await response.json();
                            return {status: "Fail", data: data};
                        }

                        if (!response.ok) {
                            console.log(response)
                            throw new Error("HTTP error " + response.status);
                        }

                        const data = await response.json();
                        if (data && data.status !== "processing") {
                            return {status: "Success", data};
                        }
                    } catch (err) {
                        console.warn("Error or still processing, retrying...", err);
                    }

                    attempts++;
                    await new Promise((resolve) => setTimeout(resolve, 2000));
                }

                return {status: "Unsuccessful", data: null};
            }

            async function checkAdminProduct(productId, cookiesText) {
                const url = "/mro-availability-api-caller/resources/product-availability-and-price-on-back-end";

                const body = new URLSearchParams({
                    productId: productId.toString(),
                    cookies: cookiesText,
                });

                let attempts = 0;
                const maxAttempts = 5;

                while (attempts < maxAttempts) {
                    try {
                        const response = await fetch(url, {
                            method: "POST",
                            headers: {"Content-Type": "application/x-www-form-urlencoded"},
                            body: body.toString(),
                        });

                        if (response.status === 403) {
                            const data = await response.json();
                            return {status: "Fail", data: data};
                        }

                        if (!response.ok) {
                            throw new Error("HTTP error " + response.status);
                        }

                        const data = await response.json();
                        if (data && data.status !== "processing") {
                            return {status: "Success", data};
                        }
                    } catch (err) {
                        console.warn("Error or still processing, retrying...", err);
                    }

                    attempts++;
                    await new Promise((resolve) => setTimeout(resolve, 2000));
                }

                return {status: "Unsuccessful", data: null};
            }


            function displayResult( {
            supplier,
                    id1,
                    id2,
                    id3,
                    result1,
                    result2,
                    result3,
                    result4,
                    result5,
                    result6,
                    result7,
                    result8,
                    result9
            }) {
                const resultsTable = document.getElementById("results");
                const resultsBody = document.getElementById("results-body");
                resultsTable.classList.remove("d-none");

                const safeTitle = (data) => {
                    if (!data)
                        return "No data";
                    return JSON.stringify(data).replace(/"/g, "'");
                };

                const tr = document.createElement("tr");
                tr.innerHTML = `
            <td>${supplier}</td>
            <td>
              <a href="https://www.mrosupply.com/-/${id1}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result1.data)}"><span class="${result1.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result1.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id2}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result2.data)}"><span class="${result2.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result2.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id3}" target="_blank" data-toggle="tooltip" data-placement="top"
                 title="${safeTitle(result3.data)}"><span class="${result3.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result3.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id1}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result4.data)}"><span class="${result4.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result4.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id2}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result5.data)}"><span class="${result5.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result5.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id3}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result6.data)}"><span class="${result6.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result6.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id1}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result7.data)}"><span class="${result7.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result7.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id2}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result8.data)}"><span class="${result8.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result8.status}</span></a>
            </td>
            <td>
              <a href="https://www.mrosupply.com/-/${id3}" target="_blank" data-toggle="tooltip" data-placement="top" 
                 title="${safeTitle(result9.data)}"><span class="${result9.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result9.status}</span></a>
            </td>
          `;
                resultsBody.appendChild(tr);
            }


async function generateRegalReport() {
    $("#report-generation-form").addClass("d-none");
    $("#generated-report").removeClass("d-none");
    $("body").removeClass("container mt-3").addClass("m-3");

    const cookiesText = document.getElementById("cookies").value.trim();
    if (!cookiesText) {
        alert("Please insert cookies with mrosupply.com session ID.");
        return;
    }

    const listingsText = document.getElementById("productsToCheck").value.trim();
    let listings = [];
    try {
        listings = JSON.parse(listingsText);
    } catch (e) {
        alert("Products JSON is not valid. Please fix the content of the Products textarea.");
        return;
    }
    if (!Array.isArray(listings) || listings.length === 0) {
        alert("Products list is empty.");
        return;
    }

    const processingDiv = document.getElementById("processing");
    const doneCounter = document.getElementById("done");
    const lengthCounter = document.getElementById("length");
    const resultsTable = document.getElementById("results");
    const resultsBody = document.getElementById("results-body");

    resultsBody.innerHTML = "";
    doneCounter.innerText = "0";
    lengthCounter.innerText = listings.length;
    resultsTable.classList.add("d-none");
    processingDiv.classList.remove("d-none");

    let counter = 0;
    for (const listing of listings) {
        counter++;
        doneCounter.innerText = counter;

        const { supplier, products } = listing;
        const results = [];
        for (const productId of products) {
            const res = await checkLoggedInProduct(productId, cookiesText);
            results.push(res);
        }
        displayLoggedInResult({ supplier, products, results });

        $(function () {
            $('[data-toggle="tooltip"]').tooltip({ container: 'body' });
        });
    }

    processingDiv.classList.add("d-none");
}

function displayLoggedInResult({ supplier, products, results }) {
    const resultsTable = document.getElementById("results");
    const resultsBody = document.getElementById("results-body");
    resultsTable.classList.remove("d-none");

    const safeTitle = (data) => {
        if (!data) return "No data";
        return JSON.stringify(data).replace(/"/g, "'");
    };

    const tr = document.createElement("tr");
    let cells = "";
    for (let i = 0; i < products.length; i++) {
        const id = products[i];
        const result = results[i];
        cells += `<td><a href="https://www.mrosupply.com/-/${id}" target="_blank" data-toggle="tooltip" data-placement="top" title="${safeTitle(result.data)}"><span class="${result.status === 'Success' ? 'badge badge-pill badge-success' : 'badge badge-pill badge-danger'}">${result.status}</span></a></td>`;
    }
    tr.innerHTML = `<td>${supplier}</td>` + cells;
    resultsBody.appendChild(tr);
}
