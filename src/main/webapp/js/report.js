            function safeTitle(data) {
                if (!data) return "No data";
                return JSON.stringify(data).replace(/"/g, "'");
            }

            function makeCell(id, result) {
                const qtyBadge = (result.data && typeof result.data.total_qty_available !== 'undefined')
                        ? `<span class="badge badge-pill ${result.data.total_qty_available > 0 ? 'badge-info' : 'badge-secondary'} ml-1">${result.data.total_qty_available}</span>`
                        : '';
                let cls = 'badge badge-pill badge-danger';
                if (result.status === 'Success' || result.status === 'Hidden') cls = 'badge badge-pill badge-success';
                else if (result.status === 'Overloaded' || result.status === 'Not authorized') cls = 'badge badge-pill badge-warning';
                return `<a href="https://www.mrosupply.com/-/${id}" target="_blank" data-toggle="tooltip" data-placement="top" title="${safeTitle(result.data)}"><span class="${cls}">${result.status}</span>${qtyBadge}</a>`;
            }

            const CONCURRENCY = 4; // how many suppliers are processed in parallel

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

                const implementedOnly = document.getElementById("implementedOnly").checked;
                if (implementedOnly) {
                    try {
                        const response = await fetch("http://mroscrape.top:4003/list-scripts");
                        const data = await response.json();
                        if (data && Array.isArray(data.scripts)) {
                            const active = new Set(data.scripts.filter(s => s.isActive).map(s => s.name));
                            listings = listings.filter(l => active.has(l.supplier));
                        }
                    } catch (err) {
                        console.warn("Failed to fetch implemented scripts", err);
                    }
                }

                /* ---------- 3. prepare UI ---------- */
                const processingDiv = document.getElementById("processing");
                const doneCounter = document.getElementById("done");
                const lengthCounter = document.getElementById("length");
                const progressBar = document.getElementById("progress-bar");
                const progressContainer = document.getElementById("progress-container");
                const resultsTable = document.getElementById("results");
                const resultsBody = document.getElementById("results-body");

                resultsBody.innerHTML = "";
                doneCounter.innerText = "0";
                lengthCounter.innerText = listings.length;
                resultsTable.classList.add("d-none");   // hidden until we have rows
                processingDiv.classList.remove("d-none");
                progressBar.style.width = "0%";
                progressBar.setAttribute("aria-valuenow", "0");
                progressContainer.classList.remove("d-none");

                /* ---------- 4. run checks with limited concurrency ---------- */
                let counter = 0;

                async function processListing(listing) {
                    counter++;
                    doneCounter.innerText = counter;
                    const percent = Math.round((counter / listings.length) * 100);
                    progressBar.style.width = percent + "%";
                    progressBar.setAttribute("aria-valuenow", percent.toString());

                    const {supplier, script, id1, id2, id3, catalog_number1, catalog_number2, catalog_number3} = listing;

                    const result1 = await checkLoggedInProduct(id1, cookiesText);
                    const result2 = await checkLoggedInProduct(id2, cookiesText);
                    const result3 = await checkLoggedInProduct(id3, cookiesText);

                    const result4 = await checkLoggedOutProduct(id1, cookiesText);
                    const result5 = await checkLoggedOutProduct(id2, cookiesText);
                    const result6 = await checkLoggedOutProduct(id3, cookiesText);

                    const result7 = await checkAdminProduct(id1, cookiesText);
                    const result8 = await checkAdminProduct(id2, cookiesText);
                    const result9 = await checkAdminProduct(id3, cookiesText);

                    const result10 = await checkAopProduct(script, catalog_number1);
                    const result11 = await checkAopProduct(script, catalog_number2);
                    const result12 = await checkAopProduct(script, catalog_number3);

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
                        result9,
                        result10,
                        result11,
                        result12
                    });

                    $(function () {
                        $('[data-toggle="tooltip"]').tooltip({container: 'body'});
                    });
                }

                for (let i = 0; i < listings.length; i += CONCURRENCY) {
                    const slice = listings.slice(i, i + CONCURRENCY);
                    const promises = slice.map(l => processListing(l));
                    await Promise.all(promises);
                }

                /* ---------- 5. finish ---------- */
                $("#report-generation-form").addClass("d-none");
                processingDiv.classList.add("d-none");
                progressContainer.classList.add("d-none");
            }

            async function checkLoggedInProduct(productId, cookiesText) {
                const url = "/mro-availability-api-caller/resources/product-availability-and-price-on-front-end";

                const body = new URLSearchParams({
                    productId: productId.toString(),
                    cookies: cookiesText,
                });

                let attempts = 0;
                const maxAttempts = 3;
                
                while (attempts < maxAttempts) {
                    try {
                        const response = await fetch(url, {
                            method: "POST",
                            headers: {"Content-Type": "application/x-www-form-urlencoded"},
                            body: body.toString(),
                        });

                        if (response.status === 403) {
                            const data = await response.json();
                            if (data && data.detail && data.detail.includes('Authentication credentials')) {
                                return {status: "Not authorized", data};
                            }
                            return {status: "Fail", data};
                        }
                        if (response.status === 429) {
                            attempts++;
                            if (attempts >= maxAttempts) {
                                return {status: "Overloaded", data: null};
                            }
                            await new Promise((resolve) => setTimeout(resolve, 5000 * attempts));
                            continue;
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
                    if (attempts < maxAttempts) {
                        await new Promise((resolve) => setTimeout(resolve, 2000));
                    }
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
                const maxAttempts = 3;

                while (attempts < maxAttempts) {
                    try {
                        const response = await fetch(url, {
                            method: "POST",
                            headers: {"Content-Type": "application/x-www-form-urlencoded"},
                            body: body.toString(),
                        });

                        if (response.status === 403) {
                            const data = await response.json();
                            if (data && data.detail && data.detail.includes('Authentication credentials')) {
                                return {status: "Hidden", data};
                            }
                            return {status: "Fail", data: data};
                        }
                        if (response.status === 429) {
                            attempts++;
                            if (attempts >= maxAttempts) {
                                return {status: "Overloaded", data: null};
                            }
                            await new Promise((resolve) => setTimeout(resolve, 5000 * attempts));
                            continue;
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
                    if (attempts < maxAttempts) {
                        await new Promise((resolve) => setTimeout(resolve, 2000));
                    }
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
                const maxAttempts = 3;

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
                        if (response.status === 429) {
                            attempts++;
                            if (attempts >= maxAttempts) {
                                return {status: "Overloaded", data: null};
                            }
                            await new Promise((resolve) => setTimeout(resolve, 5000 * attempts));
                            continue;
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
                    if (attempts < maxAttempts) {
                        await new Promise((resolve) => setTimeout(resolve, 2000));
                    }
                }

                return {status: "Unsuccessful", data: null};
            }

            async function checkAopProduct(script, catalogNumber) {
                const url = `http://mroscrape.top:4003/scrapers/${script}/availability?token=mro-high-secret&catalog_number=${encodeURIComponent(catalogNumber)}`;

                try {
                    const response = await fetch(url);
                    let data = null;
                    try {
                        data = await response.json();
                    } catch (e) {
                        data = null;
                    }

                    if (data && data.error && data.error.includes('not active')) {
                        return {status: "Not active", data};
                    }
                    if (data && data.error && data.error.includes('No module named')) {
                        return {status: "No script found", data};
                    }

                    if (!response.ok) {
                        throw new Error("HTTP error " + response.status);
                    }

                    if (data && data.result && data.result.success === false) {
                        if (data.result.error === 'Selenium Grid is not reachable') {
                            return {status: "Selenium down", data};
                        }
                        return {status: "Fail", data};
                    }

                    let total = data.total_qty_available;
                    if (typeof total === 'undefined' && data.result) {
                        if (typeof data.result.total_qty_available !== 'undefined') {
                            total = data.result.total_qty_available;
                        } else if (typeof data.result.qty_available !== 'undefined') {
                            total = data.result.qty_available;
                        } else if (data.result.availability) {
                            total = Object.values(data.result.availability)
                                    .reduce((sum, a) => sum + (a && a.qty ? a.qty : 0), 0);
                        }
                    }
                    if (typeof total !== 'undefined') {
                        data.total_qty_available = total;
                    }

                    return {status: "Success", data};
                } catch (err) {
                    console.warn("AOP request failed", err);
                    return {status: "Unsuccessful", data: null};
                }
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
                    result9,
                    result10,
                    result11,
                    result12
            }) {
                const resultsTable = document.getElementById("results");
                const resultsBody = document.getElementById("results-body");
                resultsTable.classList.remove("d-none");

                const tr = document.createElement("tr");
                tr.innerHTML = `
            <td>${supplier}</td>
            <td>${makeCell(id1, result1)}</td>
            <td>${makeCell(id2, result2)}</td>
            <td>${makeCell(id3, result3)}</td>
            <td>${makeCell(id1, result4)}</td>
            <td>${makeCell(id2, result5)}</td>
            <td>${makeCell(id3, result6)}</td>
            <td>${makeCell(id1, result7)}</td>
            <td>${makeCell(id2, result8)}</td>
            <td>${makeCell(id3, result9)}</td>
            <td>${makeCell(id1, result10)}</td>
            <td>${makeCell(id2, result11)}</td>
            <td>${makeCell(id3, result12)}</td>
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
    const progressBar = document.getElementById("progress-bar");
    const progressContainer = document.getElementById("progress-container");
    const resultsTable = document.getElementById("results");
    const resultsBody = document.getElementById("results-body");

    resultsBody.innerHTML = "";
    doneCounter.innerText = "0";
    lengthCounter.innerText = listings.length;
    resultsTable.classList.add("d-none");
    processingDiv.classList.remove("d-none");
    progressBar.style.width = "0%";
    progressBar.setAttribute("aria-valuenow", "0");
    progressContainer.classList.remove("d-none");

    let counter = 0;

    async function processRegalListing(listing) {
        counter++;
        doneCounter.innerText = counter;
        const percent = Math.round((counter / listings.length) * 100);
        progressBar.style.width = percent + "%";
        progressBar.setAttribute("aria-valuenow", percent.toString());

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

    for (let i = 0; i < listings.length; i += CONCURRENCY) {
        const slice = listings.slice(i, i + CONCURRENCY);
        const promises = slice.map(l => processRegalListing(l));
        await Promise.all(promises);
    }

    processingDiv.classList.add("d-none");
    progressContainer.classList.add("d-none");
}

function displayLoggedInResult({ supplier, products, results }) {
    const resultsTable = document.getElementById("results");
    const resultsBody = document.getElementById("results-body");
    resultsTable.classList.remove("d-none");

    const tr = document.createElement("tr");
    let cells = "";
    for (let i = 0; i < products.length; i++) {
        const id = products[i];
        const result = results[i];
        cells += `<td>${makeCell(id, result)}</td>`;
    }
    tr.innerHTML = `<td>${supplier}</td>` + cells;
    resultsBody.appendChild(tr);
}
