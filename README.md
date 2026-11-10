# Heat Diffusion in an Infinite Rod Using Fourier Transform

A capstone project for B.E. Computer Science Engineering. It solves the one-dimensional heat equation on an infinite rod with the **Fourier Transform** and shows how the temperature spreads, through an interactive web page.

- Frontend: HTML, CSS, JavaScript (Plotly charts, KaTeX equations)
- Backend: Python, Flask
- Numerics: NumPy (FFT) and SciPy (numerical integration, spline)

---

## 1. Folder structure

```
heat_diffusion_project/
├── app.py                  Flask server: routes, JSON API, error handling
├── heat_solver.py          All the mathematics + input validation
├── requirements.txt        Python packages to install
├── README.md               This file
├── EXAMINER_GUIDE.md       Simple explanation + viva questions for presentation
├── templates/
│   └── index.html          The web page (3 modules)
├── static/
│   ├── css/style.css       Styling (responsive, modern)
│   └── js/app.js           Validation, API call, charts, sliders, table, download
└── tests/
    └── test_solver.py      17 automated tests (maths, validation, Flask routes)
```

---

## 2. Install and run in VS Code

You need **Python 3.9 or newer** (check with `python --version`; on macOS/Linux use `python3`).

1. Unzip the project and open the folder: **File → Open Folder → `heat_diffusion_project`**.
2. Open a terminal: **Terminal → New Terminal**.
3. Create a virtual environment (recommended):

   | Windows (PowerShell) | macOS / Linux |
   |---|---|
   | `python -m venv venv` | `python3 -m venv venv` |
   | `venv\Scripts\Activate.ps1` | `source venv/bin/activate` |

   If PowerShell blocks the activate script, run
   `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass` once and activate again.
   When it works, the prompt starts with `(venv)`.
4. (VS Code) Press `Ctrl+Shift+P` → **Python: Select Interpreter** → choose the one inside `venv`.
5. Install the dependencies:
   ```
   pip install -r requirements.txt
   ```
6. Start the server:
   ```
   python app.py
   ```
7. Open **http://127.0.0.1:5000** in your browser. Stop the server with `Ctrl+C`.

To run the automated tests: `python -m unittest discover -s tests -v`

**Internet note.** The page loads Plotly and KaTeX from a CDN. If the CDN is unreachable, Plotly is served by Flask from the installed `plotly` package, so charts still work offline. Only the typeset equations fall back to plain LaTeX text.

**Troubleshooting**

| Problem | Fix |
|---|---|
| `ModuleNotFoundError: flask` | The venv is not active, or step 5 was skipped. |
| `Address already in use` | Another program uses port 5000. Change `port=5000` at the bottom of `app.py`. |
| Page loads but "Cannot reach the server" | `python app.py` is not running. |
| Red field messages | A value is outside its allowed range (shown under each field). |

---

## 3. Using the application

**Module 1 – Input & parameters.** Enter the initial peak temperature **A**, source sharpness **a**, thermal diffusivity **α**, simulation time **T** and a probe position. Empty fields use defaults; out-of-range values are flagged under the field and also re-checked by the server. The page shows the governing equation, the initial condition, and your current formula with your numbers substituted.

**Module 2 – Fourier simulation.** Shows the four solution steps and runs the simulation. After every run it verifies the formula in two independent ways (FFT and an inverse-transform integral) and checks that total heat is conserved.

**Module 3 – Results & analysis.**
- Temperature-vs-position graph at t = 0, T/4, T/2, 3T/4, T, plus the "selected" time curve
- **Time slider** (and ▶ Play to animate) and **position slider** with a live readout
- Temperature-vs-time chart (peak and chosen position) and a position × time heat map
- Table of calculated values (analytic and FFT columns)
- **Download CSV** of the results
- Automatic checklist: hottest at the source, spreads, peak decreases, wider and smoother

---

## 4. Mathematical model

Heat equation and initial condition (A = a = α = 1 gives the project sample):

```
u_t = α u_xx ,  −∞ < x < ∞ ,  t > 0
u(x,0) = A·exp(−a x²)
```

Fourier Transform `U(ω,t) = ∫ u(x,t) e^(−iωx) dx`:

1. Transform the PDE: `dU/dt = −α ω² U` (derivatives in x become multiplication by `iω`)
2. Transform the data: `U(ω,0) = A·√(π/a)·exp(−ω²/(4a))`
3. Solve the ODE: `U(ω,t) = A·√(π/a)·exp(−ω²(αt + 1/(4a)))`
4. Invert (Gaussian integral):

```
u(x,t) = A / √(1 + 4aαt) · exp( −a x² / (1 + 4aαt) )
```

For A = a = α = 1: `u(x,t) = e^(−x²/(1+4t)) / √(1+4t)`, and `U(ω,t) = √π · e^(−ω²(t+1/4))`, exactly the project's sample.

Useful derived quantities (used by the UI): peak `u(0,t) = A/√(1+4aαt)`, spread `σ(t) = √((1+4aαt)/(2a))`, and total heat `∫u dx = A√(π/a)` (constant in time).

---

## 5. How the code works

### `heat_solver.py` (the mathematics)

| Function | What it does |
|---|---|
| `parse_params()` | Converts raw input to numbers, applies defaults, rejects non-numbers, NaN/∞ and out-of-range values (raises `ValidationError`). Limits live in the `FIELDS` table, which also fills the HTML form. |
| `initial_spectrum()`, `spectrum_at_time()` | Steps 2–3: `U(ω,0)` and `U(ω,t)`. |
| `analytic_solution()` | Step 4: the closed-form `u(x,t)`. This is what is plotted. |
| `fft_solution()` | **Fourier Transform implemented numerically**: sample `u(x,0)` on a grid → `np.fft.fft` → multiply each frequency by `exp(−α k² t)` → `np.fft.ifft`. |
| `inverse_fourier_quad()` | **Inverse transform as an integral**: `u(x,t) = (1/π)∫₀^∞ U(ω,t) cos(ωx) dω` evaluated with `scipy.integrate.quad` at the probe point. |
| `simulate()` | Builds 41 time frames, peak and spread curves, the three verification checks and the value table. |
| `build_csv()` | Creates the downloadable CSV. |

Why `fft_solution` is the same method as the pen-and-paper one: the FFT turns the grid values into amplitudes of waves `e^(ikx)`. For each wave the heat equation becomes `dU/dt = −αk²U`, whose solution is `U·e^(−αk²t)`. The inverse FFT adds the waves back together. High frequencies (sharp features) are damped fastest, which is why the profile becomes smoother. The grid is chosen wide enough (±6 spreads) that the periodic assumption of the FFT never matters, and fine enough to resolve the narrow starting hot spot.

### `app.py` (the server)

`GET /` renders the page. `POST /api/simulate` validates and returns the results as JSON. `GET /api/download` returns the CSV. Error handlers turn bad input into `400` JSON messages (`{"error": "..."}`), unknown API paths into JSON `404`, and unexpected failures into a safe `500`.

### `static/js/app.js` (the page logic)

Validates the form → `fetch('/api/simulate')` → draws charts with Plotly. The sliders only read the already-downloaded frames, so they respond instantly. Every number on screen comes from the backend, so the display always matches the mathematics.

---

## 6. Sample output (defaults: A = 1, a = 1, α = 1, T = 2)

Checked values from a real run of the project:

| x (m) | t = 0 | t = 0.5 | t = 1.0 | t = 1.5 | t = 2.0 |
|---:|---:|---:|---:|---:|---:|
| −2.121 | 0.011109 | 0.128824 | 0.181823 | 0.198729 | 0.202177 |
| −1.061 | 0.324652 | 0.396807 | 0.357107 | 0.321850 | 0.294166 |
| **0.000** | **1.000000** | **0.577350** | **0.447214** | **0.377964** | **0.333333** |
| 1.061 | 0.324652 | 0.396807 | 0.357107 | 0.321850 | 0.294166 |
| 2.121 | 0.011109 | 0.128824 | 0.181823 | 0.198729 | 0.202177 |
| 3.182 | 0.000040 | 0.019756 | 0.059029 | 0.088976 | 0.108217 |

(The table in the app lists 11 positions. The FFT column at t = 2 matches these values to about 1e-8.)

Summary and verification:

```
Peak temperature   t=0: 1.0000   →  t=2: 0.3333   (= 1/√9)
Spread σ           t=0: 0.7071   →  t=2: 2.1213   (3× wider)
FFT (1024 points) vs formula, max error      : 5.1e-09
Inverse integral at x=1, t=2                 : 0.2982797723 vs formula 0.2982797723
Total heat ∫u dx   t=0: 1.77245   t=2: 1.77234   exact A√π = 1.77245
```

Reading the table: at t = 0 all heat is at x = 0 (u = 1). As t grows the centre cools (1 → 0.577 → 0.447 → 0.378 → 0.333) while positions away from the centre warm up (x = 2.121: 0.011 → 0.202). The curve becomes wider, lower and smoother, which is the expected physics. The area under the curve stays √π, so heat is only redistributed, not lost.

---

## 7. Limits of the model

- The rod is ideal: infinite, homogeneous, no heat loss to surroundings, constant α.
- The initial profile is a Gaussian (so the Fourier Transform has an exact closed form). Other initial profiles would use the same FFT method, but their closed form would differ.
- Allowed ranges: A 0.01–1000, a 0.1–10, α 0.01–5, T 0.1–20. The plot shows x within ±4 spreads of the final profile (about 99.99 % of the heat), so the on-screen heat total differs from the exact value by around 1e-4 relative.
