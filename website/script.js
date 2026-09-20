const API_BASE_URL = "/api/backend";
let latestInspectionImageData = null;

// ==========================================
// FIREBASE ANONYMOUS LOGIN
// ==========================================

async function ensureFirebaseLogin() {

    let user = firebase.auth().currentUser;

    if (!user) {

        const result =
            await firebase.auth().signInAnonymously();

        user = result.user;

        console.log(
            "Firebase anonymous login successful:",
            user.uid
        );
    }

    return user;
}


// ==========================================
// GET FIREBASE ID TOKEN
// ==========================================

async function getFirebaseIdToken() {

    const user =
        await ensureFirebaseLogin();

    const idToken =
        await user.getIdToken(true);

    console.log(
        "Firebase ID token obtained."
    );

    return idToken;
}


// ==========================================
// SYNC FIREBASE USER WITH PYAZLENS
// ==========================================

async function syncFirebaseUser() {

    const user =
        await ensureFirebaseLogin();

    const idToken =
        await user.getIdToken(true);

    const formData =
        new FormData();

    formData.append(
        "id_token",
        idToken
    );

    formData.append(
        "name",
        localStorage.getItem("user_name") ||
        user.displayName ||
        "Website User"
    );

    formData.append(
        "address",
        localStorage.getItem("user_address") ||
        ""
    );

    const response =
        await fetch(
            `${API_BASE_URL}/auth/firebase`,
            {
                method: "POST",
                body: formData
            }
        );

    const data =
        await response.json();

    console.log(
        "FIREBASE AUTH RESPONSE:",
        data
    );

    if (!response.ok) {

        throw new Error(
            data.detail ||
            "Firebase authentication failed."
        );
    }

    if (data.user_profile_id) {

        localStorage.setItem(
            "user_profile_id",
            String(data.user_profile_id)
        );
    }

    return data;
}
// ==========================================
// LOAD REAL INSIGHTS FROM BACKEND
// SAME LOGIC AS ANDROID STATS SCREEN
// ==========================================

async function loadInsights() {

    try {

        // ------------------------------------------
        // FIREBASE LOGIN
        // ------------------------------------------

        const user =
            await ensureFirebaseLogin();


        // ------------------------------------------
        // GET FIREBASE TOKEN
        // ------------------------------------------

        const idToken =
            await user.getIdToken(true);


        // ------------------------------------------
        // SYNC USER WITH PYAZLENS
        // ------------------------------------------

        const authData =
            await syncFirebaseUser();


        const userProfileId =
            authData.user_profile_id ||
            localStorage.getItem("user_profile_id");


        if (!userProfileId) {

            throw new Error(
                "User profile ID not found."
            );

        }


        // ------------------------------------------
        // GET USER INSPECTION HISTORY
        // ------------------------------------------

        const response =
            await fetch(
                `${API_BASE_URL}/users/${userProfileId}/inspections`,
                {
                    method: "GET",

                    headers: {
                        Authorization:
                            `Bearer ${idToken}`
                    }
                }
            );


        const data =
            await response.json();


        console.log(
            "REAL INSPECTION HISTORY:",
            data
        );


        if (!response.ok) {

            throw new Error(
                data.detail ||
                "Unable to load inspection history."
            );

        }


        // ------------------------------------------
        // SAME AS ANDROID:
        // response.inspections
        // ------------------------------------------

        const inspections =
            data.inspections || [];


        // ------------------------------------------
        // EMPTY STATE
        // ------------------------------------------

        if (inspections.length === 0) {

            updateInsight(
                "insightInspections",
                "0"
            );

            updateInsight(
                "insightTotal",
                "0"
            );

            return;

        }


        // ==========================================
        // CALCULATE TOTALS
        // SAME AS ANDROID
        // ==========================================

        const totalInspections =
            inspections.length;


        const totalOnions =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.total_onions || 0
                    ),
                0
            );


        const gradeA =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.grade_a_count || 0
                    ),
                0
            );


        const urs =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.urs_count || 0
                    ),
                0
            );


        const rejected =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.rejected_count || 0
                    ),
                0
            );


        // ==========================================
        // DEFECT COUNTS
        // SAME AS ANDROID
        // ==========================================

        const rotten =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.rotten_count || 0
                    ),
                0
            );


        const sprouted =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.sprouted_count || 0
                    ),
                0
            );


        const cutCrack =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.cut_crack_count || 0
                    ),
                0
            );


        const skinDamage =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.skin_damage_count || 0
                    ),
                0
            );


        const sunburned =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.sunburned_count || 0
                    ),
                0
            );


        const misshapen =
            inspections.reduce(
                (sum, inspection) =>
                    sum +
                    Number(
                        inspection.misshapen_count || 0
                    ),
                0
            );


        // ==========================================
        // PERCENTAGES
        // SAME AS ANDROID percentage()
        // ==========================================

        const gradeAPercentage =
            calculatePercentage(
                gradeA,
                totalOnions
            );


        const ursPercentage =
            calculatePercentage(
                urs,
                totalOnions
            );


        const rejectedPercentage =
            calculatePercentage(
                rejected,
                totalOnions
            );


        // ==========================================
        // UPDATE OVERVIEW
        // ==========================================

        updateInsight(
            "insightInspections",
            totalInspections.toString()
        );


        updateInsight(
            "insightTotal",
            totalOnions.toLocaleString()
        );


        // ==========================================
        // UPDATE QUALITY
        // ==========================================

        updateInsight(
            "insightGradeA",
            `${gradeA} onions · ${gradeAPercentage.toFixed(0)}%`
        );


        updateInsight(
            "insightURS",
            `${urs} onions · ${ursPercentage.toFixed(0)}%`
        );


        updateInsight(
            "insightRejected",
            `${rejected} onions · ${rejectedPercentage.toFixed(0)}%`
        );


        // ==========================================
        // UPDATE DEFECTS
        // ==========================================

        updateInsight(
            "insightRotten",
            rotten.toString()
        );


        updateInsight(
            "insightSprouted",
            sprouted.toString()
        );


        updateInsight(
            "insightCutCrack",
            cutCrack.toString()
        );


        updateInsight(
            "insightSkinDamage",
            skinDamage.toString()
        );


        updateInsight(
            "insightSunburned",
            sunburned.toString()
        );


        updateInsight(
            "insightMisshapen",
            misshapen.toString()
        );


        // ==========================================
        // CONSOLE CHECK
        // ==========================================

        console.log(
            "PYAZLENS INSIGHTS:",
            {
                totalInspections,
                totalOnions,
                gradeA,
                urs,
                rejected,
                rotten,
                sprouted,
                cutCrack,
                skinDamage,
                sunburned,
                misshapen,
                gradeAPercentage,
                ursPercentage,
                rejectedPercentage
            }
        );


    } catch (error) {

        console.error(
            "INSIGHTS ERROR:",
            error
        );

    }

}


// ==========================================
// PERCENTAGE
// SAME AS ANDROID
// ==========================================

function calculatePercentage(
    value,
    total
) {

    if (total <= 0) {

        return 0;

    }

    return (
        Number(value) *
        100
    ) / Number(total);

}


// ==========================================
// UPDATE HTML ELEMENT
// ==========================================

function updateInsight(
    id,
    value
) {

    const element =
        document.getElementById(id);


    if (element) {

        element.innerText =
            value;

    }

}
// ==========================================
// ANALYZE ONION IMAGE
// ==========================================

async function analyzeImage() {

    const imageInput =
        document.getElementById("imageInput");

    const result =
        document.getElementById("result");


    // ==========================================
    // CHECK IMAGE
    // ==========================================

    if (imageInput.files.length === 0) {

        result.innerText =
            "Please select an onion image first.";

        return;
    }


    // ==========================================
    // FIREBASE AUTHENTICATION
    // ==========================================

    let idToken;

    try {

        idToken =
            await getFirebaseIdToken();

    } catch (error) {

        console.error(
            "FIREBASE AUTH ERROR:",
            error
        );

        result.innerHTML = `
            <div class="analysis-error">
                <h3>Authentication Error</h3>
                <p>${error.message}</p>
            </div>
        `;

        return;
    }


    // ==========================================
    // SYNC USER WITH PYAZLENS
    // ==========================================

    try {

        await syncFirebaseUser();

    } catch (error) {

        console.error(
            "PYAZLENS AUTH ERROR:",
            error
        );

        result.innerHTML = `
            <div class="analysis-error">
                <h3>Authentication Failed</h3>
                <p>${error.message}</p>
            </div>
        `;

        return;
    }


    // ==========================================
    // GET IMAGE
    // ==========================================

    const file =
        imageInput.files[0];
    // Save uploaded image for PDF
    const reader = new FileReader();
    
    reader.onload = function (e) {
        latestInspectionImageData = e.target.result;
    };
    
    reader.readAsDataURL(file);


    const formData =
        new FormData();


    formData.append(
        "file",
        file
    );


    formData.append(
        "name",
        localStorage.getItem("user_name") ||
        "Website User"
    );


    // ==========================================
    // LOADING SCREEN
    // ==========================================

    result.innerHTML = `
        <div class="ai-loading">

            <div class="ai-loading-title">
                <span class="ai-pulse"></span>
                AI ANALYSIS IN PROGRESS
            </div>

            <div class="scan-graph">
                <div class="graph-line"></div>
                <div class="scan-dot"></div>
            </div>

            <h3>
                Analyzing onion quality...
            </h3>

            <p>
                Detecting defects, size and quality
            </p>

            <div class="progress-container">
                <div class="analysis-progress"></div>
            </div>

        </div>
    `;


    // ==========================================
    // CALL BACKEND
    // ==========================================

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/analyze`,
                {
                    method: "POST",

                    headers: {
                        Authorization:
                            `Bearer ${idToken}`
                    },

                    body: formData
                }
            );


        const data =
            await response.json();


        console.log(
            "REAL API RESPONSE:",
            data
        );


        // ==========================================
        // API ERROR
        // ==========================================

        if (!response.ok) {

            result.innerHTML = `
                <div class="analysis-error">

                    <h3>
                        Analysis Failed
                    </h3>

                    <p>
                        ${
                            data.detail ||
                            "Unable to analyze the image."
                        }
                    </p>

                </div>
            `;

            return;
        }


        // ==========================================
        // SAVE LATEST RESULT
        // ==========================================

        localStorage.setItem(
            "latest_inspection",
            JSON.stringify(data)
        );


        // ==========================================
        // GET RESULT DATA
        // ==========================================

        const summary =
            data.summary || {};


        const onions =
            data.onions || [];


        const originalImageURL =
            URL.createObjectURL(file);


        // ==========================================
        // COUNTS
        // ==========================================

        const totalOnions =
            Number(data.total_onions || 0);


        const gradeA =
            Number(summary.grade_a || 0);


        const urs =
            Number(summary.grade_urs || 0);


        const rejected =
            Number(summary.rejected || 0);


        const gradeAPercentage =
            calculatePercentage(
                gradeA,
                totalOnions
            );


        const ursPercentage =
            calculatePercentage(
                urs,
                totalOnions
            );


        const rejectedPercentage =
            calculatePercentage(
                rejected,
                totalOnions
            );


        // ==========================================
        // INSPECTION RESULT HTML
        // ==========================================

        let html = `

            <div class="inspection-result">


                <!-- HEADER -->

                <div class="result-header">

                    <button
                        onclick="document.getElementById('result').innerHTML=''"
                    >
                        ←
                    </button>

                    <div>

                        <h2>
                            Inspection Result
                        </h2>

                        <p>
                            ${totalOnions} onions analyzed
                        </p>

                    </div>

                    <button
                        onclick="downloadResultPDF()"
                    >
                        📄
                    </button>

                </div>


                <!-- VISUAL INSPECTION -->

                <div class="result-card visual-inspection">

                    <h2>
                        AI Visual Inspection
                    </h2>

                    <p>
                        Tap an onion to view its detailed result
                    </p>


                    <div class="inspection-image-container">

                        <canvas
                            id="inspectionCanvas"
                        ></canvas>

                    </div>


                    <!-- LEGEND -->

                    <div class="quality-legend">

                        <span>

                            <span
                                class="legend-dot grade-a-dot"
                            ></span>

                            Grade A

                        </span>


                        <span>

                            <span
                                class="legend-dot urs-dot"
                            ></span>

                            URS

                        </span>


                        <span>

                            <span
                                class="legend-dot rejected-dot"
                            ></span>

                            Reject

                        </span>

                    </div>

                </div>


                <!-- DONUT / QUALITY -->

                <div class="result-card">

                    <h2>
                        Quality Overview
                    </h2>


                    <div class="result-donut">

                        <div
                            class="donut-center"
                        >

                            ${
                                gradeAPercentage.toFixed(0)
                            }%

                            <small>
                                Good
                            </small>

                        </div>

                    </div>


                    <div class="result-stats">

                        <div class="result-stat">

                            <strong>
                                ${totalOnions}
                            </strong>

                            <span>
                                Total Onions
                            </span>

                            <small>
                                100%
                            </small>

                        </div>


                        <div class="result-stat">

                            <strong>
                                ${gradeA}
                            </strong>

                            <span>
                                Grade A
                            </span>

                            <small>
                                ${gradeAPercentage.toFixed(0)}%
                            </small>

                        </div>


                        <div class="result-stat">

                            <strong>
                                ${urs}
                            </strong>

                            <span>
                                Grade URS
                            </span>

                            <small>
                                ${ursPercentage.toFixed(0)}%
                            </small>

                        </div>


                        <div class="result-stat">

                            <strong>
                                ${rejected}
                            </strong>

                            <span>
                                Reject
                            </span>

                            <small>
                                ${rejectedPercentage.toFixed(0)}%
                            </small>

                        </div>

                    </div>

                </div>


                <!-- INDIVIDUAL ONION DETAILS -->

                <div class="result-card">

                    <h2>
                        Individual Onion Details
                    </h2>

        `;


        // ==========================================
        // EACH ONION
        // ==========================================

        onions.forEach(
            (onion, index) => {

                const size =
                    onion.size || {};


                const defects =
                    onion.defects || [];


                const defectNames =
                    defects.map(
                        defect =>
                            typeof defect === "string"
                                ? defect
                                : defect.name
                    );


                const cropUrl =
                    onion.crop_url?.startsWith("http")
                        ? onion.crop_url
                        : API_BASE_URL + onion.crop_url;


                html += `

                    <div class="individual-onion">

                        <div class="onion-heading">

                            <h3>
                                Onion ${index + 1}
                            </h3>

                            <span class="onion-grade">

                                ${
                                    onion.grade ||
                                    "N/A"
                                }

                            </span>

                        </div>


                        <!-- CROP IMAGE -->

                        ${
                            onion.crop_url
                                ? `
                                    <img
                                        src="${cropUrl}"
                                        class="onion-result-image"
                                        alt="Onion ${index + 1}"
                                    >
                                  `
                                : ""
                        }


                        <!-- CLASSIFICATION -->

                        <p>

                            <strong>
                                Classification:
                            </strong>

                            ${
                                onion.classification ||
                                "N/A"
                            }

                        </p>


                        <!-- DEFECTS -->

                        <p>

                            <strong>
                                Defects:
                            </strong>

                            ${
                                defectNames.length > 0
                                    ? defectNames.join(", ")
                                    : "No defect"
                            }

                        </p>


                        <!-- SIZE -->

                        <div class="onion-measurements">

                            <div>

                                <strong>
                                    Diameter
                                </strong>

                                <span>
                                    ${
                                        size.diameter_mm ??
                                        "N/A"
                                    } mm
                                </span>

                            </div>


                            <div>

                                <strong>
                                    Width
                                </strong>

                                <span>
                                    ${
                                        size.width_mm ??
                                        "N/A"
                                    } mm
                                </span>

                            </div>


                            <div>

                                <strong>
                                    Height
                                </strong>

                                <span>
                                    ${
                                        size.height_mm ??
                                        "N/A"
                                    } mm
                                </span>

                            </div>

                        </div>


                        <!-- GRADE REASON -->

                        <div class="grade-reason">

                            <strong>
                                Why this grade?
                            </strong>

                            <p style="font-size: 30px;">
                                ${
                                    onion.grade_reason ||
                                    "N/A"
                                }
                            </p>

                        </div>


                        <!-- PROBABILITIES -->

                        <details>

                            <summary>
                                View probabilities
                            </summary>

                            <div>

                `;


                const probabilities =
                    onion.probabilities || {};


                Object.entries(
                    probabilities
                ).forEach(
                    ([defect, probability]) => {

                        const percentage =
                            (
                                Number(probability) * 100
                            ).toFixed(2);


                        html += `

                            <p>

                                ${defect}:
                                ${percentage}%

                            </p>

                        `;

                    }
                );


                html += `

                            </div>

                        </details>

                    </div>

                `;

            }
        );


        // ==========================================
        // CLOSE RESULT
        // ==========================================

        html += `

                </div>


                <!-- DOWNLOAD PDF -->

                <button
                    class="download-pdf-btn"
                    onclick="downloadResultPDF()"
                >
                    📄 Download Result as PDF
                </button>


            </div>

        `;


        // ==========================================
        // SHOW RESULT
        // ==========================================

        result.innerHTML =
            html;


        // ==========================================
        // DRAW ONION IMAGE + OUTLINES
        // ==========================================

        drawInspectionImage(
            originalImageURL,
            onions
        );


    } catch (error) {

        console.error(
            "API ERROR:",
            error
        );


        result.innerHTML = `

            <div class="analysis-error">

                <h3>
                    Connection Error
                </h3>

                <p>
                    ${
                        error.message ||
                        "Unable to connect to the PyazLens server."
                    }
                </p>

            </div>

        `;

    }

}


// ==========================================
// DOWNLOAD RESULT AS PDF
// ==========================================

function downloadResultPDF() {

    const result = document.getElementById("result");

    if (!result || !result.innerHTML.trim()) {
        alert("No inspection result available.");
        return;
    }

    const printWindow = window.open("", "_blank");

    if (!printWindow) {
        alert("Please allow pop-ups to download the PDF.");
        return;
    }

    const clonedResult = result.cloneNode(true);
    /* Copy donut color into PDF */
    const originalDonut = result.querySelector(".result-donut");
    const clonedDonut = clonedResult.querySelector(".result-donut");

    if (originalDonut && clonedDonut) {

    clonedDonut.style.background =
        getComputedStyle(originalDonut).backgroundImage;

    clonedDonut.style.backgroundColor =
        getComputedStyle(originalDonut).backgroundColor;

    clonedDonut.style.borderRadius = "50%";

    clonedDonut.style.clipPath =
        "circle(50% at 50% 50%)";
}
    /*
    ==========================================
    REMOVE BLANK CANVAS
    ==========================================
    */

    const canvases = clonedResult.querySelectorAll("canvas");

    canvases.forEach(canvas => {

        if (latestInspectionImageData) {

            const img = document.createElement("img");

            img.src = latestInspectionImageData;

            img.className = "pdf-main-inspection-image";

            canvas.replaceWith(img);

        } else {

            canvas.remove();

        }

    });


    printWindow.document.write(`

<!DOCTYPE html>

<html>

<head>

<title>PyazLens Inspection Report</title>

<style>

* {
    box-sizing: border-box;
}

@page {
    size: A4;
    margin: 12mm;
}

body {
    font-family: Arial, sans-serif;
    margin: 0;
    background: white;
    color: #333;
    font-size: 11px;
}


/* ==========================================
   HEADER
========================================== */

.pdf-header {
    display: flex;
    align-items: center;
    gap: 10px;
    padding-bottom: 12px;
    border-bottom: 3px solid #55106d;
    margin-bottom: 16px;
}

.pdf-header img {
    width: 42px !important;
    height: 42px !important;
    object-fit: contain;
}

.pdf-header h1 {
    margin: 0;
    color: #55106d;
    font-size: 25px;
}


/* ==========================================
   TITLE
========================================== */

.pdf-title {
    margin-bottom: 18px;
}

.pdf-title h2 {
    margin: 0;
    color: #24102f;
    font-size: 20px;
}

.pdf-title p {
    color: #777;
    font-size: 10px;
    margin-top: 4px;
}


/* ==========================================
   RESULT
========================================== */

.inspection-result {
    width: 100% !important;
    max-width: none !important;
    margin: 0 !important;
}

.result-card {
    background: white !important;
    border: 1px solid #e3dbe8 !important;
    border-radius: 10px !important;
    padding: 15px !important;
    margin-bottom: 13px !important;
    box-shadow: none !important;

    page-break-inside: avoid !important;
    break-inside: avoid !important;
}

.result-card h3 {
    color: #55106d !important;
    font-size: 16px !important;
    margin-bottom: 5px !important;
}

.result-card > p {
    color: #777 !important;
    font-size: 10px !important;
}


/* ==========================================
   ACTUAL INSPECTION IMAGE
========================================== */

.inspection-image-container {
    width: 100% !important;
    max-width: 620px !important;

    margin: 12px auto !important;
    padding: 7px !important;

    background: #faf8fb !important;

    border: 1px solid #ddd !important;

    border-radius: 8px !important;

    text-align: center !important;
}


/*
   THIS IS THE IMPORTANT FIX
*/

.pdf-main-inspection-image {

    display: block !important;

    width: auto !important;

    max-width: 100% !important;

    height: 330px !important;

    max-height: 330px !important;

    object-fit: contain !important;

    margin: 0 auto !important;

    border-radius: 6px !important;

}


/* ==========================================
   LEGEND
========================================== */

.quality-legend {

    display: flex !important;

    justify-content: center !important;

    gap: 25px !important;

    margin: 9px 0 !important;

    font-size: 10px !important;

}


/* ==========================================
   PDF QUALITY DONUT — FORCE CIRCLE
========================================== */

.result-donut {
    width: 145px !important;
    height: 145px !important;

    min-width: 145px !important;
    min-height: 145px !important;

    max-width: 145px !important;
    max-height: 145px !important;

    display: block !important;

    position: relative !important;

    margin: 15px auto !important;

    padding: 0 !important;

    border-radius: 50% !important;

    clip-path: circle(50% at 50% 50%) !important;

    overflow: hidden !important;

    aspect-ratio: 1 / 1 !important;

    flex-shrink: 0 !important;

    -webkit-print-color-adjust: exact !important;
    print-color-adjust: exact !important;
}


/* White center */

.result-donut::before {
    content: "" !important;

    position: absolute !important;

    width: 90px !important;
    height: 90px !important;

    left: 50% !important;
    top: 50% !important;

    transform: translate(-50%, -50%) !important;

    background: #ffffff !important;

    border-radius: 50% !important;

    z-index: 5 !important;

    display: block !important;
}


/* Center percentage */

.result-donut .donut-center {
    position: absolute !important;

    width: 90px !important;
    height: 90px !important;

    left: 50% !important;
    top: 50% !important;

    transform: translate(-50%, -50%) !important;

    display: flex !important;

    flex-direction: column !important;

    justify-content: center !important;

    align-items: center !important;

    text-align: center !important;

    z-index: 10 !important;

    margin: 0 !important;

    padding: 0 !important;
}


.result-donut .donut-center strong {
    display: block !important;

    font-size: 21px !important;

    line-height: 1.1 !important;

    margin: 0 !important;

    color: #3f124e !important;
}


.result-donut .donut-center span {
    display: block !important;

    font-size: 9px !important;

    line-height: 1.2 !important;

    margin-top: 4px !important;

    color: #777 !important;
}

/* ==========================================
   STATISTICS
========================================== */

.result-stats {

    display: grid !important;

    grid-template-columns: repeat(4, 1fr) !important;

    gap: 8px !important;

}

.result-stat {

    padding: 11px 6px !important;

    border-radius: 8px !important;

    box-shadow: none !important;

}

.result-stat strong {

    font-size: 20px !important;

}


/* ==========================================
   GRADE COLORS
========================================== */

.grade-a,
.onion-grade.grade-a {

    color: #2F8F2F !important;

    background: #EAF7E5 !important;

    border: 1px solid #BFE5B5 !important;

}

.grade-urs,
.onion-grade.grade-urs {

    color: #A96800 !important;

    background: #FFF4D6 !important;

    border: 1px solid #F1D28A !important;

}

.rejected,
.grade-reject,
.onion-grade.rejected,
.onion-grade.grade-reject {

    color: #B52F2F !important;

    background: #FDEAEA !important;

    border: 1px solid #F0B5B5 !important;

}


/* ==========================================
   DEFECTS
========================================== */

.defect-grid {

    display: grid !important;

    grid-template-columns: repeat(3, 1fr) !important;

    gap: 7px !important;

}

.defect-grid div {

    padding: 10px 6px !important;

    border-radius: 7px !important;

}


/* ==========================================
   MEASUREMENTS
========================================== */

.measurement-grid {

    display: grid !important;

    grid-template-columns: repeat(3, 1fr) !important;

    gap: 7px !important;

}

.measurement-grid div {

    padding: 11px 6px !important;

    border-radius: 7px !important;

}


/* ==========================================
   INDIVIDUAL ONIONS
========================================== */

.individual-onion {

    display: block !important;

    width: 100% !important;

    padding: 12px !important;

    margin-top: 10px !important;

    border: 1px solid #e3dbe8 !important;

    border-radius: 9px !important;

    box-shadow: none !important;

    page-break-inside: avoid !important;

    break-inside: avoid !important;

}


.onion-heading {

    display: flex !important;

    justify-content: space-between !important;

    align-items: center !important;

    margin-bottom: 8px !important;

}


.onion-heading h4 {

    font-size: 14px !important;

    margin: 0 !important;

}


/* SMALL ONION IMAGE */

.onion-result-image {

    display: block !important;

    width: 100px !important;

    height: 100px !important;

    max-width: 100px !important;

    max-height: 100px !important;

    object-fit: contain !important;

    margin: 5px auto 9px !important;

    padding: 3px !important;

    border: 1px solid #ddd !important;

    border-radius: 8px !important;

}


/* ==========================================
   ONION DETAILS
========================================== */

.onion-measurements {

    display: grid !important;

    grid-template-columns: repeat(3, 1fr) !important;

    gap: 5px !important;

}

.onion-measurements span {

    padding: 6px 3px !important;

    font-size: 9px !important;

    text-align: center !important;

}

.grade-reason {

    margin-top: 7px !important;

    padding: 8px 10px !important;

    font-size: 9px !important;

}


/* ==========================================
   HIDE BUTTONS
========================================== */

button,
.download-pdf-btn {

    display: none !important;

}


/* ==========================================
   PRINT COLORS
========================================== */

* {

    -webkit-print-color-adjust: exact !important;

    print-color-adjust: exact !important;

}

</style>

</head>


<body>


<div class="pdf-header">

    <img
        src="image/logo.png"
        alt="PyazLens Logo"
    >

    <h1>PyazLens</h1>

</div>


<div class="pdf-title">

    <h2>
        AI Onion Quality Inspection Report
    </h2>

    <p>
        AI-powered onion quality analysis and inspection results
    </p>

</div>


<div class="pdf-section-label">
    <span class="label-icon">01</span>
    AI Visual Inspection
    <small>Scanned Onion Image</small>
</div>

${clonedResult.innerHTML}


</body>

</html>

    `);

    printWindow.document.close();


    /*
    ==========================================
    WAIT FOR IMAGES
    ==========================================
    */

    setTimeout(() => {

        const images =
            printWindow.document.images;

        Promise.all(

            Array.from(images).map(img => {

                if (img.complete) {
                    return Promise.resolve();
                }

                return new Promise(resolve => {

                    img.onload = resolve;

                    img.onerror = resolve;

                });

            })

        ).then(() => {

            setTimeout(() => {

                printWindow.focus();

                printWindow.print();

            }, 500);

        });

    }, 500);

}
async function drawInspectionImage(originalImageURL, onions) {

    const canvas = document.getElementById("inspectionCanvas");

    if (!canvas) {
        console.error("inspectionCanvas not found");
        return;
    }

    const ctx = canvas.getContext("2d");

    // If backend does not provide original image URL,
    // use the image selected by the user.
    if (!originalImageURL) {

        const imageInput = document.getElementById("imageInput");

        if (!imageInput || !imageInput.files[0]) {
            console.error("Original image not found");
            return;
        }

        originalImageURL =
            URL.createObjectURL(imageInput.files[0]);
    }

    const img = new Image();

    img.onload = function () {

        const container =
            document.querySelector(".inspection-image-container");

        const maxWidth =
            container ? container.clientWidth : 700;

        const scale =
            Math.min(1, maxWidth / img.width);

        canvas.width = img.width * scale;
        canvas.height = img.height * scale;

        ctx.drawImage(
            img,
            0,
            0,
            canvas.width,
            canvas.height
        );

        if (!Array.isArray(onions)) {
            return;
        }

        onions.forEach((onion, index) => {

            let grade =
                String(onion.grade || "").toUpperCase();

            let color = "#D94A4A";

            if (
                grade.includes("GRADE A") ||
                grade === "A"
            ) {
                color = "#73C943";
            }
            else if (
                grade.includes("URS")
            ) {
                color = "#D49320";
            }

            const segmentation =
                onion.segmentation;

            if (!segmentation) {
                return;
            }

            let points = [];

            // Format: [[x,y], [x,y], ...]
            if (
                Array.isArray(segmentation) &&
                Array.isArray(segmentation[0])
            ) {
                points = segmentation;
            }

            // Format: [{x,y}, {x,y}, ...]
            else if (
                Array.isArray(segmentation) &&
                typeof segmentation[0] === "object"
            ) {
                points = segmentation.map(p => [
                    p.x,
                    p.y
                ]);
            }

            if (points.length < 2) {
                return;
            }

            ctx.beginPath();

            points.forEach((point, i) => {

                const x = point[0] * scale;
                const y = point[1] * scale;

                if (i === 0) {
                    ctx.moveTo(x, y);
                } else {
                    ctx.lineTo(x, y);
                }

            });

            ctx.closePath();

            // Transparent fill
            ctx.fillStyle = color + "33";
            ctx.fill();

            // Colored outline
            ctx.strokeStyle = color;
            ctx.lineWidth = 4;
            ctx.stroke();

            // Onion number
            const firstPoint = points[0];

            const labelX =
                firstPoint[0] * scale;

            const labelY =
                firstPoint[1] * scale;

            ctx.beginPath();

            ctx.arc(
                labelX,
                labelY,
                16,
                0,
                Math.PI * 2
            );

            ctx.fillStyle = color;
            ctx.fill();

            ctx.fillStyle = "#ffffff";
            ctx.font = "bold 14px Arial";
            ctx.textAlign = "center";
            ctx.textBaseline = "middle";

            ctx.fillText(
                index + 1,
                labelX,
                labelY
            );

        });

    };

    img.onerror = function () {
        console.error(
            "Could not load inspection image:",
            originalImageURL
        );
    };

    img.src = originalImageURL;
}