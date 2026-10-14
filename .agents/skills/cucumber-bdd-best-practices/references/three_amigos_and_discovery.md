# Discovery, Formulation, and the Three Amigos

## The Three Amigos Workshop
In Behavior-Driven Development (BDD), specifications originate in collaborative conversations known as **Discovery Workshops** or **Three Amigos Meetings**:

- **Product Owner / Business Analyst**: Defines the business goal, user requirements, and success metrics (*What problem are we solving?*).
- **Developer**: Identifies technical implications, feasibility, system architecture, and integrations (*How will we build it?*).
- **Quality Engineer (QA)**: Questions edge cases, negative flows, boundary conditions, and testability (*What could go wrong?*).

---

## The Three Core Practices

```
Discovery ──(Concrete Examples)──> Formulation ──(Gherkin Specs)──> Automation
```

### 1. Discovery
Focus on concrete examples rather than abstract rules:
- *Instead of*: "Users get a discount when buying multiple items."
- *Ask*: "If John buys 3 items on a Tuesday with a coupon, what exact discount is applied?"

### 2. Formulation
Convert agreed examples into clear Gherkin feature files:
- Use domain terms familiar to both business and technical team members (Ubiquitous Language).
- Ensure each scenario tells a clear story with `Given` (Context), `When` (Action), and `Then` (Expected Outcome).

### 3. Automation
Connect the formulated specifications to automated test code:
- Run tests as part of continuous integration (CI) pipelines.
- Living documentation guarantees that when code changes, tests and documentation stay in sync.
