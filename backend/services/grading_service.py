# ============================================================
# PyazLens Grading Service
# ============================================================

GRADE_A_MIN_MM = 45.0
GRADE_A_MAX_MM = 65.0

# TODO: Set these to the actual applicable URS limits.
URS_MIN_MM = 0.0
URS_MAX_MM = 1000.0

DEFECT_THRESHOLD = 0.50


def grade_onion(onion):
    """
    Grade one onion.

    Priority:
        1. REJECT
        2. Grade A
        3. Grade URS
    """

    diameter_mm = onion["size"]["diameter_mm"]
    probabilities = onion.get("probabilities", {})

    rotten = probabilities.get("Rotten", 0.0) >= DEFECT_THRESHOLD
    cut_crack = probabilities.get("Cut/Crack", 0.0) >= DEFECT_THRESHOLD
    sprouted = probabilities.get("Sprouted", 0.0) >= DEFECT_THRESHOLD
    skin_damage = probabilities.get("Skin Damage", 0.0) >= DEFECT_THRESHOLD
    sunburned = probabilities.get("Sunburned", 0.0) >= DEFECT_THRESHOLD
    misshapen = probabilities.get("Misshapen", 0.0) >= DEFECT_THRESHOLD

    # ========================================================
    # 1. REJECT — CHECK FIRST
    # ========================================================

    if rotten:
        return {
            "grade": "REJECT",
            "reason": "Rotten"
        }

    if cut_crack:
        return {
            "grade": "REJECT",
            "reason": "Cut/Crack"
        }

    if sprouted:
        return {
            "grade": "REJECT",
            "reason": "Sprouted"
        }

    # Outside applicable URS range
    if not (URS_MIN_MM <= diameter_mm <= URS_MAX_MM):
        return {
            "grade": "REJECT",
            "reason": "Size outside acceptable range"
        }

    # ========================================================
    # 2. GRADE A
    # ========================================================

    grade_a_size = (
        GRADE_A_MIN_MM
        <= diameter_mm
        <= GRADE_A_MAX_MM
    )

    all_defects_clear = not (
        rotten
        or cut_crack
        or sprouted
        or skin_damage
        or sunburned
        or misshapen
    )

    if grade_a_size and all_defects_clear:
        return {
            "grade": "Grade A",
            "reason": "Meets all Grade A requirements"
        }

    # ========================================================
    # 3. GRADE URS
    # ========================================================

    urs_size = (
        URS_MIN_MM
        <= diameter_mm
        <= URS_MAX_MM
    )

    # At this point Rotten, Cut/Crack and Sprouted have
    # already been checked and are absent.
    #
    # Skin Damage, Sunburned and Misshapen are allowed.

    if urs_size:
        return {
            "grade": "Grade URS",
            "reason": "Does not meet Grade A but has only allowed defects"
        }

    # ========================================================
    # FALLBACK
    # ========================================================

    return {
        "grade": "REJECT",
        "reason": "Does not meet Grade A or Grade URS requirements"
    }


# ============================================================
# BATCH SUMMARY
# ============================================================

def summarize_batch(onions):
    """
    Generate batch-level grading statistics.
    """

    total = len(onions)

    grade_a_count = 0
    urs_count = 0
    rejected_count = 0

    defect_summary = {
        "Rotten": 0,
        "Cut/Crack": 0,
        "Sprouted": 0,
        "Skin Damage": 0,
        "Sunburned": 0,
        "Misshapen": 0
    }

    # --------------------------------------------------------
    # Count grades and defects
    # --------------------------------------------------------

    for onion in onions:

        grade = onion.get("grade")

        if grade == "Grade A":
            grade_a_count += 1

        elif grade == "Grade URS":
            urs_count += 1

        elif grade == "REJECT":
            rejected_count += 1

        probabilities = onion.get(
            "probabilities",
            {}
        )

        for defect_name in defect_summary:

            probability = probabilities.get(
                defect_name,
                0.0
            )

            if probability >= DEFECT_THRESHOLD:
                defect_summary[defect_name] += 1

    # --------------------------------------------------------
    # Percentages
    # --------------------------------------------------------

    if total > 0:

        grade_a_percentage = (
            grade_a_count / total
        ) * 100

        urs_percentage = (
            urs_count / total
        ) * 100

        rejected_percentage = (
            rejected_count / total
        ) * 100

    else:

        grade_a_percentage = 0.0
        urs_percentage = 0.0
        rejected_percentage = 0.0

    return {

        "total_onions": total,

        "grade_counts": {
            "Grade A": grade_a_count,
            "Grade URS": urs_count,
            "REJECT": rejected_count
        },

        "grade_percentages": {
            "Grade A": round(
                grade_a_percentage,
                2
            ),
            "Grade URS": round(
                urs_percentage,
                2
            ),
            "REJECT": round(
                rejected_percentage,
                2
            )
        },

        "defect_summary": defect_summary
    }