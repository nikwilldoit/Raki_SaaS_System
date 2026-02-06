import React, { useEffect, useMemo, useState } from "react";
import "./CalendarPicker.css";

const buildCalendarDays = (year, month) => {
  // Build a full weeks grid (previous and next month spillover) for the given month
  const firstDayOfMonth = new Date(year, month, 1);
  const startWeekday = firstDayOfMonth.getDay(); // 0 (Sun) - 6 (Sat) finds what day of the week the month starts on
  const daysInMonth = new Date(year, month + 1, 0).getDate(); // One day before the 1st of next month
  const daysInPrevMonth = new Date(year, month, 0).getDate(); // Same for previous month

  const days = [];

  // Fill in days from previous month
  for (let i = startWeekday - 1; i >= 0; i--) {

    const day = daysInPrevMonth - i;
    const date = new Date(year, month - 1, day);

    days.push({ 
      date, 
      label: day, 
      currentMonth: false // Mark as outside current month
    });
  }

  // Fill in days for current month
  for (let d = 1; d <= daysInMonth; d++) {

    const date = new Date(year, month, d);

    days.push({ 
      date, 
      label: d, // The text label representing the day
      currentMonth: true // Mark as inside current month
    });
  }

  // Fill in days for next month to complete the last week
  while (days.length % 7 !== 0) {

    const day = days.length - (startWeekday + daysInMonth) + 1;
    const date = new Date(year, month + 1, day);

    days.push({ 
      date, 
      label: date.getDate(), 
      currentMonth: false 
    });
  }

  return days;
};

const CalendarPicker = ({ selectedDate, onSelectDate, availableDates }) => {

  const initialViewDate = selectedDate ? new Date(selectedDate) : new Date();

  const [viewDate, setViewDate] = useState(initialViewDate);
  
  // Anchor "today" at local midnight for comparisons
  const today = useMemo(() => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  }, []);

  // A date is selectable only if it's today/future and included in availableDates (when provided)
  // Available dates are expected to be in ISO string format (YYYY-MM-DD)
  // Checks if the shop is open on that date, not for timeslots
  const isDateAvailable = useMemo(() => {

    const availableSet = availableDates ? new Set(availableDates) : null;

    // Return a function that checks availability for a given ISO date string
    return (isoDate) => {

      const dateObj = new Date(isoDate);

      const isTodayOrFuture = dateObj >= today;

      // Means that shop is open for that date
      const hasSlot = availableSet ? availableSet.has(isoDate) : true; // default to available when not provided
      
      return isTodayOrFuture && hasSlot;
    };
  }, [availableDates, today]); // Dependency array for this memo

  // Sync viewDate when selectedDate changes from the outside component(ReservationCreatePage)
  useEffect(() => {
    if (selectedDate) {
      setViewDate(new Date(selectedDate));
    }
  }, [selectedDate]); 

  const viewYear = viewDate.getFullYear();
  const viewMonth = viewDate.getMonth();

  const calendarDays = useMemo(
    () => buildCalendarDays(viewYear, viewMonth),
    [viewMonth, viewYear]
  );

  // delta is either -1 or 1
  const goMonth = (delta) => {
    // prev is the viewDate before clicking to change month
    setViewDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + delta, 1));
  };

  const handleSelect = (date, currentMonth) => {
    if (!currentMonth) return;
    
    // GOOD: Construct local YYYY-MM-DD
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const localDateString = `${year}-${month}-${day}`;

    onSelectDate?.(localDateString);
    setViewDate(new Date(date));
  };

  return (
    <div className="calendar-card">
      <div className="calendar-header">
        <button
          type="button"
          className="calendar-nav"
          onClick={() => goMonth(-1)}
          aria-label="Previous month"
        >
          ‹
        </button>
        <div className="calendar-month">
          {new Intl.DateTimeFormat("en-US", {
            month: "long",
            year: "numeric",
          }).format(viewDate)}
        </div>
        <button
          type="button"
          className="calendar-nav"
          onClick={() => goMonth(1)}
          aria-label="Next month"
        >
          ›
        </button>
      </div>
      <div className="calendar-grid calendar-weekdays">
        {["S", "M", "T", "W", "T", "F", "S"].map((day) => (
          <div key={day} className="calendar-weekday">
            {day}
          </div>
        ))}
      </div>
      <div className="calendar-grid calendar-days">
        {calendarDays.map(({ date, label, currentMonth }) => {
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, "0");
          const day = String(date.getDate()).padStart(2, "0");
          const iso = `${year}-${month}-${day}`;
          const isSelected = selectedDate === iso;
          
          // "Today" check to ignore time components
          const isToday = new Date().toDateString() === date.toDateString();
          
          const available = isDateAvailable(iso);
          
          // Allow selecting "today" if it is available
          const isSelectable = currentMonth && available;
          return (
            <button
              key={`${iso}-${currentMonth}`}
              type="button"
              className={`calendar-day ${
                currentMonth ? "" : "outside"
              } ${isSelected ? "selected" : ""} ${isToday ? "today" : ""} ${
                available ? "" : "disabled"
              }`}
              onClick={() => {
                if (!isSelectable) return;
                handleSelect(date, currentMonth);
              }}
            >
              {label}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default CalendarPicker;
