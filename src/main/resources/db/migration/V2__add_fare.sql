alter table bus_schedules
    add fare DECIMAL(10, 2) default 0.00 not null;