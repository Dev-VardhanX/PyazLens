const API_BASE_URL = "http://152.67.10.2:8000";


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
    // GET FIREBASE ID TOKEN
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

                <h3>
                    Authentication Error
                </h3>

                <p>
                    ${error.message}
                </p>

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

                <h3>
                    Authentication Failed
                </h3>

                <p>
                    ${error.message}
                </p>

            </div>

        `;

        return;
    }


    // ==========================================
    // GET SELECTED FILE
    // ==========================================

    const file =
        imageInput.files[0];


    // ==========================================
    // CREATE FORMDATA
    // ==========================================

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
    // AI LOADING ANIMATION
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
    // SEND IMAGE TO FASTAPI
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
        // SAVE REAL INSPECTION
        // ==========================================

        localStorage.setItem(
            "latest_inspection",
            JSON.stringify(data)
        );


        // ==========================================
        // GET DATA
        // ==========================================

        const summary =
            data.summary || {};

        const onions =
            data.onions || [];


        // ==========================================
        // RESULT HTML
        // ==========================================

        let html = `

            <div class="analysis-result">

                <h2>
                    Inspection Result
                </h2>


                <div class="result-summary">


                    <div>

                        <strong>
                            Total Onions
                        </strong>

                        <span>
                            ${data.total_onions ?? 0}
                        </span>

                    </div>


                    <div>

                        <strong>
                            Grade A
                        </strong>

                        <span>
                            ${summary.grade_a ?? 0}
                        </span>

                    </div>


                    <div>

                        <strong>
                            URS
                        </strong>

                        <span>
                            ${summary.grade_urs ?? 0}
                        </span>

                    </div>


                    <div>

                        <strong>
                            Rejected
                        </strong>

                        <span>
                            ${summary.rejected ?? 0}
                        </span>

                    </div>


                    <div>

                        <strong>
                            Rejected %
                        </strong>

                        <span>
                            ${summary.rejected_percentage ?? 0}%
                        </span>

                    </div>


                </div>


                <h3>
                    Onion Details
                </h3>

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


                html += `

                    <div class="onion-result">

                        <h3>
                            Onion ${index + 1}
                        </h3>


                        <p>

                            <strong>
                                Classification:
                            </strong>

                            ${
                                onion.classification ||
                                "N/A"
                            }

                        </p>


                        <p>

                            <strong>
                                Grade:
                            </strong>

                            ${
                                onion.grade ||
                                "N/A"
                            }

                        </p>


                        <p>

                            <strong>
                                Diameter:
                            </strong>

                            ${
                                size.diameter_mm ??
                                "N/A"
                            } mm

                        </p>


                        <p>

                            <strong>
                                Width:
                            </strong>

                            ${
                                size.width_mm ??
                                "N/A"
                            } mm

                        </p>


                        <p>

                            <strong>
                                Height:
                            </strong>

                            ${
                                size.height_mm ??
                                "N/A"
                            } mm

                        </p>


                        <p>

                            <strong>
                                Grade Reason:
                            </strong>

                            ${
                                onion.grade_reason ||
                                "N/A"
                            }

                        </p>


                        <p>

                            <strong>
                                Defects:
                            </strong>

                            ${
                                defects.length > 0
                                    ? defects.join(", ")
                                    : "No defect"
                            }

                        </p>


                        <details>

                            <summary>
                                Probabilities
                            </summary>

                            <div>

                `;


                // ==========================================
                // DEFECT PROBABILITIES
                // ==========================================

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
        // DOWNLOAD PDF BUTTON
        // ==========================================

        html += `

            <button
                class="download-pdf-btn"
                onclick="downloadResultPDF()"
            >
                📄 Download Result as PDF
            </button>

            </div>

        `;


        // ==========================================
        // SHOW REAL RESULT
        // ==========================================

        result.innerHTML =
            html;


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
                    ${error.message ||
                    "Unable to connect to the PyazLens server."}
                </p>

            </div>

        `;

    }

}


// ==========================================
// DOWNLOAD RESULT AS PDF
// ==========================================

function downloadResultPDF() {

    const result =
        document.getElementById("result");


    if (
        !result ||
        !result.innerHTML.trim()
    ) {

        alert(
            "No inspection result available."
        );

        return;
    }


    const printWindow =
        window.open(
            "",
            "_blank"
        );


    if (!printWindow) {

        alert(
            "Please allow pop-ups to download the PDF."
        );

        return;
    }


    printWindow.document.write(`

        <!DOCTYPE html>

        <html>

        <head>

            <title>
                PyazLens Inspection Report
            </title>


            <style>
                .pdf-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.pdf-header img {
    width: 38px !important;
    height: 38px !important;
    max-width: 38px !important;
    max-height: 38px !important;
    object-fit: contain;
}

.pdf-header h1 {
    margin: 0;
    font-size: 28px;
    font-weight: 700;
}

                body {

                    font-family:
                        Arial,
                        sans-serif;

                    padding: 30px;

                    color: #333;

                }


                h1,
                h2,
                h3 {

                    color: #4B145F;

                }


                .analysis-result {

                    max-width: 900px;

                    margin: auto;

                }


                .result-summary {

                    display: grid;

                    grid-template-columns:
                        repeat(5, 1fr);

                    gap: 10px;

                    margin: 20px 0;

                }


                .result-summary > div {

                    padding: 15px;

                    border: 1px solid #ddd;

                    border-radius: 10px;

                    text-align: center;

                }


                .result-summary strong {

                    display: block;

                    font-size: 12px;

                    color: #666;

                }


                .result-summary span {

                    display: block;

                    margin-top: 8px;

                    font-size: 20px;

                    font-weight: bold;

                    color: #4B145F;

                }


                .onion-result {

                    margin: 15px 0;

                    padding: 18px;

                    border: 1px solid #ddd;

                    border-radius: 10px;

                    page-break-inside: avoid;

                }


                p {

                    line-height: 1.5;

                }


                details {

                    margin-top: 10px;

                }


                button {

                    display: none;

                }

            </style>

        </head>


        <body>

            <div class="pdf-header">
                <img src="image/logo.png" alt="PyazLens Logo">
                <h1>PyazLens</h1>
            </div>  

            <h2>
                AI Onion Quality Inspection Report
            </h2>


            ${result.innerHTML}


        </body>

        </html>

    `);


    printWindow.document.close();


    setTimeout(
        () => {

            printWindow.print();

        },
        500
    );

}
// ==========================================
// LOAD INSIGHTS WHEN PAGE OPENS
// ==========================================

window.addEventListener(
    "DOMContentLoaded",
    () => {

        loadInsights();

    }
);