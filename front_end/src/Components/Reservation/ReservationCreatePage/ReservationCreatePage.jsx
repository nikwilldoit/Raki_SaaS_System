import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ReservationCreatePage.css";
import CalendarPicker from "./CalendarPicker";
import Header from "../../Header/Header";

// --- HELPER FUNCTIONS ---
const formatReadableDate = (dateStr) => {
  if (!dateStr) return "";
  try {
    return new Intl.DateTimeFormat("en-US", {
      month: "long",
      day: "numeric",
      year: "numeric",
    }).format(new Date(dateStr));
  } catch {
    return dateStr;
  }
};

const formatTimeLabel = (timeStr) => {
  if (!timeStr) return "";
  try {
    const [hours, minutes] = timeStr.split(":").map(Number);
    const tempDate = new Date();
    tempDate.setHours(hours, minutes, 0, 0);
    return new Intl.DateTimeFormat("en-US", {
      hour: "numeric",
      minute: "2-digit",
    }).format(tempDate);
  } catch {
    return timeStr;
  }
};

const formatDuration = (minutes) => {
  if (minutes === null || minutes === undefined) return "Duration not set";
  const hrs = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hrs && mins) return `${hrs}h ${mins}m`;
  if (hrs) return `${hrs}h`;
  return `${mins}m`;
};

const emptyCustomerInfo = { name: "", phone: "", email: "" };

// --- MAIN COMPONENT ---
const ReservationCreatePage = ({
  onBack,
  onNext,
  onClose,
  onLogout,
  userData,
}) => {
  const navigate = useNavigate();

  // --- STATE ---
  const [customerInfo, setCustomerInfo] = useState(emptyCustomerInfo);
  
  // Data lists
  const [services, setServices] = useState([]);
  const [employees, setEmployees] = useState([]);
  
  // Selections
  const [selectedServiceId, setSelectedServiceId] = useState(null);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().slice(0, 10));
  const [selectedTime, setSelectedTime] = useState("");
  
  // Calculated Data
  const [availableTimeslots, setAvailableTimeslots] = useState([]);
  
  // UI State
  const [employeeOffset, setEmployeeOffset] = useState(0);
  const [serviceOffset, setServiceOffset] = useState(0);
  
  // Status
  const [loadingMeta, setLoadingMeta] = useState(false);
  const [loadingAvailability, setLoadingAvailability] = useState(false);
  const [submitStatus, setSubmitStatus] = useState({
    status: "idle", // 'idle', 'submitting', 'success', 'error'
    message: "",
  });
  const [errorMessage, setErrorMessage] = useState("");

  const employeesPerView = 3;
  const servicesPerView = 3;

  const handle = (fn) => (fn ? fn() : undefined);

  const getAuthToken = () =>
    localStorage.getItem("authToken") || userData?.token || "";

  // --- MEMOS ---
  const selectedService = useMemo(
    () => services.find((service) => service.id === selectedServiceId),
    [services, selectedServiceId]
  );

  const selectedEmployee = useMemo(
    () => employees.find((employee) => employee.id === selectedEmployeeId),
    [employees, selectedEmployeeId]
  );

  const hasEmployeeSelection =
    selectedEmployeeId !== null && selectedEmployeeId !== undefined;

  // --- CHECK SUCCESS STATE ---
  // This is the variable we use to toggle the views
  const isSuccess = submitStatus.status === "success";

  // --- EFFECTS ---
  useEffect(() => {
    const token = getAuthToken();
    if (!token) {
      setErrorMessage("Missing auth token. Please login again.");
      return;
    }

    const fetchMetadata = async () => {
      setLoadingMeta(true);
      setErrorMessage("");
      try {
        const params = new URLSearchParams({
          businessId: userData?.businessId || "",
        });
        const response = await fetch(
          `http://localhost:8080/api/reservations/new?${params.toString()}`,
          {
            method: "GET",
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        if (!response.ok) {
          setErrorMessage("Failed to fetch reservation options.");
          setServices([]);
          setEmployees([]);
          return;
        }

        const responseJson = await response.json();
        const backendData = responseJson.data;

          const serviceList = Array.isArray(backendData.services)
              ? backendData.services.map((service) => ({
                  id: service.serviceId,
                  name: service.name,
                  durationMinutes: service.durationMinutes,
                  price: service.price,
                  displayPrice: service.displayPrice ?? service.price,
              }))
              : [];

        const employeeList = Array.isArray(backendData.employees)
          ? backendData.employees.map((employee) => ({
              id: employee.id,
              name: employee.name,
            }))
          : [];

        const employeesWithAny =
          employeeList.length && employeeList[0]?.id === 0
            ? employeeList
            : [{ id: 0, name: "Any available" }, ...employeeList];

        setServices(serviceList);
        setEmployees(employeesWithAny);
        
        // Auto-select "Any" if available and nothing else selected
        setSelectedEmployeeId((prev) =>
          prev === null || prev === undefined
            ? employeesWithAny[0]?.id ?? null
            : prev
        );
      } catch (err) {
        console.error("Error fetching reservation metadata:", err);
        setErrorMessage("Error fetching reservation options.");
      } finally {
        setLoadingMeta(false);
      }
    };

    fetchMetadata();
  }, [userData]);

  // Reset time when service changes
  useEffect(() => {
    setSelectedDate("");
    setSelectedTime("");
    setAvailableTimeslots([]);
  }, [selectedServiceId]);

  // Fetch Availability
  useEffect(() => {
    const token = getAuthToken();
    if (!token) return;

    if (!selectedServiceId || !selectedDate) {
      setAvailableTimeslots([]);
      setSelectedTime("");
      return;
    }

    const controller = new AbortController();

    const fetchAvailability = async () => {
      setLoadingAvailability(true);
      setErrorMessage("");
      try {
        const params = new URLSearchParams({
          employeeId: selectedEmployeeId ?? 0,
          date: selectedDate,
          businessId: userData?.businessId || "",
        });

        const response = await fetch(
          `http://localhost:8080/api/reservations/availability?${params.toString()}`,
          {
            method: "GET",
            headers: { Authorization: `Bearer ${token}` },
            signal: controller.signal,
          }
        );

        if (!response.ok) {
          setErrorMessage("Failed to load availability for that selection.");
          setAvailableTimeslots([]);
          setSelectedTime("");
          return;
        }

        const data = await response.json();
        const slots = Array.isArray(data) ? data : [];

        setAvailableTimeslots(slots);
        
        if (selectedTime && !slots.includes(selectedTime)) {
          setSelectedTime("");
        }
      } catch (err) {
        if (err.name !== "AbortError") {
          console.error("Error fetching availability:", err);
          setErrorMessage("Error loading availability.");
        }
      } finally {
        setLoadingAvailability(false);
      }
    };

    fetchAvailability();
    return () => controller.abort();
  }, [selectedDate, selectedEmployeeId, selectedServiceId, userData]);

  // --- VALIDATION & HELPERS ---
  const customerComplete =
    customerInfo.name && (customerInfo.phone || customerInfo.email);
  const serviceEmployeeComplete = Boolean(selectedService) && hasEmployeeSelection;
  const timeComplete = selectedDate && selectedTime;

  const steps = [
    { label: "Customer Info", complete: Boolean(customerComplete) },
    { label: "Service & Employee", complete: Boolean(serviceEmployeeComplete) },
    { label: "Time Slot", complete: Boolean(timeComplete) },
    { label: "Confirmation", complete: Boolean(timeComplete) },
  ];

  const firstIncompleteIndex = steps.findIndex((step) => !step.complete);
  const activeStepIndex =
    firstIncompleteIndex === -1 ? steps.length - 1 : firstIncompleteIndex;

  const completedStepsCount = steps.filter((step) => step.complete).length;
  const progressWidth =
    steps.length > 1
      ? `${(Math.min(completedStepsCount, steps.length - 1) / (steps.length - 1)) * 100}%`
      : "0%";

  const maxEmployeeOffset = Math.max(employees.length - employeesPerView, 0);
  const maxServiceOffset = Math.max(services.length - servicesPerView, 0);

  const handleEmployeeNav = (delta) => {
    setEmployeeOffset((prev) => Math.min(Math.max(prev + delta, 0), maxEmployeeOffset));
  };

  const handleServiceNav = (delta) => {
    setServiceOffset((prev) => Math.min(Math.max(prev + delta, 0), maxServiceOffset));
  };

  const visibleEmployees = employees.slice(employeeOffset, employeeOffset + employeesPerView);
  const visibleServices = services.slice(serviceOffset, serviceOffset + servicesPerView);

  const reviewAppointment = {
    service: selectedService?.name || "Not selected",
    dateTime:
      selectedDate && selectedTime
        ? `${formatReadableDate(selectedDate)} at ${formatTimeLabel(selectedTime)}`
        : "Not selected",
    employee:
      selectedEmployeeId === 0
        ? "Any available"
        : selectedEmployee?.name || "Not selected",
    status: isSuccess ? "Confirmed" : "Pending",
  };

  const handleCustomerChange = (field, value) => {
    setCustomerInfo((prev) => ({ ...prev, [field]: value }));
  };

  const canSubmit = Boolean(
    customerComplete && serviceEmployeeComplete && timeComplete
  );

  const handleCreateReservation = async () => {
    if (!canSubmit) {
      setSubmitStatus({
        status: "error",
        message: "Please complete all required fields before submitting.",
      });
      return;
    }

    const token = getAuthToken();
    if (!token) {
      setSubmitStatus({ status: "error", message: "Missing auth token." });
      return;
    }

    setSubmitStatus({ status: "submitting", message: "" });

    const combinedDateTime = `${selectedDate} ${selectedTime}`;
    const payload = {
      businessId: userData?.businessId || 0,
      serviceId: selectedServiceId,
      employeeId: selectedEmployeeId ?? 0,
      date: combinedDateTime,
      customerName: customerInfo.name,
      customerPhone: customerInfo.phone || "",
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/reservations/new",
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        }
      );

      if (response.status === 201) {
        setSubmitStatus({
          status: "success",
          message: "Reservation created successfully!",
        });
        // We do NOT call onNext here automatically anymore, 
        // because we want to show the Summary View within this component.
      } else {
        const errorText = await response.text();
        setSubmitStatus({
          status: "error",
          message: errorText || "Failed to create reservation.",
        });
      }
    } catch (err) {
      console.error("Error creating reservation:", err);
      setSubmitStatus({
        status: "error",
        message: "Error creating reservation.",
      });
    }
  };

  const availabilityReady = serviceEmployeeComplete;

  return (
    <div className="appointment-page">
      <Header userData={userData} onLogout={onLogout} />

      {/* LOGIC: 
        If isSuccess is FALSE (meaning !isSuccess is true), show the FORM.
        If isSuccess is TRUE, this entire section is skipped.
      */}
      {!isSuccess && (
        <section className="card wizard-card">
          <header className="card-header">
            <h2>New Reservation</h2>
          </header>

          {loadingMeta && (
            <div className="status-text">Loading services and employees...</div>
          )}
          {errorMessage && (
            <div className="status-text error-text" role="alert">{errorMessage}</div>
          )}
          {submitStatus.status === "error" && submitStatus.message && (
            <div className="status-text error-text" role="alert">
              {submitStatus.message}
            </div>
          )}

          <div className="stepper">
            <div className="stepper-track">
              <div className="stepper-progress" style={{ width: progressWidth }} />
            </div>
            <div className="stepper-steps">
              {steps.map((step, index) => {
                const isActive = index === activeStepIndex;
                return (
                  <div
                    className={`step ${
                      step.complete ? "completed" : isActive ? "active" : "upcoming"
                    }`}
                    key={step.label}
                  >
                    <div className="step-circle">{index + 1}</div>
                    <span className="step-label">{step.label}</span>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="section-stack">
            {/* 1. Customer Info */}
            <div className="form-section">
              <h3>Customer Information</h3>
              <div className="form-grid">
                <div className="form-field">
                  <label className="field-label">Name</label>
                  <input
                    className="input-field"
                    type="text"
                    value={customerInfo.name}
                    onChange={(e) => handleCustomerChange("name", e.target.value)}
                    placeholder="Enter full name"
                  />
                </div>
                <div className="form-field">
                  <label className="field-label">Phone</label>
                  <input
                    className="input-field"
                    type="tel"
                    value={customerInfo.phone}
                    onChange={(e) => handleCustomerChange("phone", e.target.value)}
                    placeholder="(555) 123-4567"
                  />
                </div>
                <div className="form-field">
                  <label className="field-label">Email</label>
                  <input
                    className="input-field"
                    type="email"
                    value={customerInfo.email}
                    onChange={(e) => handleCustomerChange("email", e.target.value)}
                    placeholder="customer@example.com"
                  />
                </div>
              </div>
            </div>

            <div className="section-divider" />

            {/* 2. Services */}
            <div className="form-section">
              <div className="section-header">
                <h3>Select Service</h3>
                <div className="section-actions">
                  <button
                    type="button"
                    className="nav-button"
                    onClick={() => handleServiceNav(-1)}
                    disabled={serviceOffset === 0}
                  >
                    ‹
                  </button>
                  <button
                    type="button"
                    className="nav-button"
                    onClick={() => handleServiceNav(1)}
                    disabled={serviceOffset >= maxServiceOffset}
                  >
                    ›
                  </button>
                </div>
              </div>
              <div className="service-grid">
                {visibleServices.map((service) => (
                  <div
                    className={`service-card ${
                      service.id === selectedServiceId ? "selected" : ""
                    }`}
                    key={service.id}
                    onClick={() =>
                      setSelectedServiceId((prev) =>
                        prev === service.id ? null : service.id
                      )
                    }
                  >
                    <div className="service-name">{service.name}</div>
                      <div className="service-price">${(service.displayPrice ?? service.price).toFixed(2)}</div>                    <div className="service-duration">
                      {formatDuration(service.durationMinutes)}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="section-divider" />

            {/* 3. Employees */}
            <div className="form-section">
              <div className="section-header">
                <h3>Select Employee</h3>
                <div className="section-actions">
                  <button
                    type="button"
                    className="nav-button"
                    onClick={() => handleEmployeeNav(-1)}
                    disabled={employeeOffset === 0}
                  >
                    ‹
                  </button>
                  <button
                    type="button"
                    className="nav-button"
                    onClick={() => handleEmployeeNav(1)}
                    disabled={employeeOffset >= maxEmployeeOffset}
                  >
                    ›
                  </button>
                </div>
              </div>
              <div className="employee-grid">
                {visibleEmployees.map((employee) => (
                  <div
                    className={`employee-card ${
                      employee.id === selectedEmployeeId ? "selected" : ""
                    }`}
                    key={employee.id}
                    onClick={() =>
                      setSelectedEmployeeId((prev) =>
                        prev === employee.id ? null : employee.id
                      )
                    }
                  >
                    <div className="employee-name">{employee.name}</div>
                  </div>
                ))}
              </div>
            </div>

            <div className="section-divider" />

            {/* 4. Calendar & Time */}
            <div className="form-section">
              <h3>Select Date &amp; Time Slot</h3>
              <div className="date-time-layout">
                <CalendarPicker
                  selectedDate={selectedDate}
                  availableDates={availabilityReady ? undefined : []}
                  onSelectDate={(iso) => {
                    setSelectedDate(iso);
                    setSelectedTime("");
                  }}
                />

                <div className="times-panel">
                  <div className="times-heading">
                    {!availabilityReady
                      ? "Select service & employee first"
                      : selectedDate
                      ? `Times for ${formatReadableDate(selectedDate)}`
                      : "Select a date"}
                  </div>
                  <div className="timeslot-grid three-column">
                    {loadingAvailability && (
                      <div className="timeslot-empty">Loading...</div>
                    )}
                    {!loadingAvailability &&
                      availabilityReady &&
                      selectedDate &&
                      availableTimeslots.map((slot) => (
                        <button
                          key={slot}
                          type="button"
                          className={`timeslot ${
                            selectedTime === slot ? "selected" : ""
                          }`}
                          onClick={() =>
                            setSelectedTime((prev) => (prev === slot ? "" : slot))
                          }
                        >
                          <div className="timeslot-time">{formatTimeLabel(slot)}</div>
                        </button>
                      ))}
                    {availabilityReady && selectedDate && !availableTimeslots.length && !loadingAvailability && (
                      <div className="timeslot-empty">No slots available.</div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="card-actions">
            <button
              type="button"
              className="ghost-button"
              onClick={() => handle(onBack)}
            >
              Back
            </button>
            <button
              type="button"
              className="primary-button"
              disabled={!canSubmit || submitStatus.status === "submitting"}
              onClick={handleCreateReservation}
            >
              {submitStatus.status === "submitting"
                ? "Creating..."
                : "Create Reservation"}
            </button>
          </div>
        </section>
      )}

      {/* LOGIC: 
        If isSuccess is TRUE, show this SUMMARY card.
      */}
      {isSuccess && (
        <section className="card details-card">
          <header className="card-header">
            <h2>Reservation Confirmed</h2>
            <button
              className="close-button"
              type="button"
              aria-label="Close"
              onClick={() => handle(onClose)}
            >
              ×
            </button>
          </header>

          <div className="status-text success-text" style={{ textAlign: "center", marginBottom: "1rem" }}>
             ✅ {submitStatus.message}
          </div>

          <div className="details-content">
            <div className="details-column">
              <h4>Customer Information</h4>
              <dl>
                <dt>Name</dt>
                <dd>{customerInfo.name || "Not provided"}</dd>
                <dt>Phone</dt>
                <dd>{customerInfo.phone || "Not provided"}</dd>
                <dt>Email</dt>
                <dd>{customerInfo.email || "Not provided"}</dd>
              </dl>
            </div>

            <div className="details-column">
              <h4>Appointment Details</h4>
              <dl>
                <dt>Service</dt>
                <dd>{reviewAppointment.service}</dd>
                <dt>Date &amp; Time</dt>
                <dd>{reviewAppointment.dateTime}</dd>
                <dt>Assigned Employee</dt>
                <dd>{reviewAppointment.employee}</dd>
                <dt>Status</dt>
                <dd className="status confirmed">Confirmed</dd>
              </dl>
            </div>
          </div>

          <div className="card-actions">
            <button
              type="button"
              className="primary-button"
              onClick={() => navigate("/dashboard")}
            >
              Done
            </button>
          </div>
        </section>
      )}
    </div>
  );
};

export default ReservationCreatePage;