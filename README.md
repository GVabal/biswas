the idea is to create events that trigger logic,
which in turn triggers other events
easy to trigger next event either automatically by previous event or manually by http endpoint or some callback event

the order, the flow of events is defined somehow
benefit of defining flow in code is that it is compiled together with all logic and it is "deployed" when application is running.
so new updated flows with logic will be active atomically.

TODO:
handle "migrations"
handle expirations of taks (timers like in camunda, when task should be cancelled automatically for example)

there is master queue which orchestrates the order of what event is being triggered next. (queue is being consumed with some kind of throttling, to reduce chaos)
every time event finishes its job, engine decides to which listener send next. it should know from some kind of definition map where to go next
linked collection?

engine event queue should be redundant (database tables)
task listeners can actually be just tasks which are triggered by service calls. super fast
another queue is going to be needed for failed tasks

how to handle migration scenarios?
usually tasks consolidate on certain checkpoints only, where they are waiting for some callback.
when appending, removing, inserting events - update flow definition
when process variables have to be altered or inserted - event name, action, variable name, variable value (if needed) and it will be updated for all currently active cases which are waiting to be handled by that listener


THE START

establish sales case

! CUSTOMER CREATION
get customer info
exists? -> continue
does not exist? -> create customer, wait 10 times, continue or raise snow incident

! CREDIT DECISION
open human task: make credit decision
accepted? -> save output from task, update status, update expected delivery date, continue
rejected? -> update status, END
expired? -> update status, END

! SOC
validate soc data
valid? -> continue
invalid? -> raise snow incident
get and save credit frame id from LC, credit limits for customer
get and save existing involved parties from LC
prepare RPA payload
wait for LC working hours
call RPA
wait for callback, while waiting if it is taking too long, update internal status that rpa sla is breached but continue waiting
save rpa response for audit
agreement created? -> save agreement number, continue
agreement was not created? -> update status
  decide whether to retry automatically (goto prepare RPA payload) or open human task
  open human task: handle LC errors
  when task handled -> goto prepare RPA payload

! AGREEMENT DOCUMENT CREATION
request to generate document
wait for callback
success? -> save output and continue
failure? -> raise snow incident

! AGREEMENT DSP
validate dsp
all good? -> continue
not good? -> open human task: handle invalid DSP
when task handled -> goto validate dsp
request to create DSP case
update status
double::
  wait for dsp callback
  subscribe to email notifications, END
dsp successful? -> update status, communicate about agreement signed, continue
dsp not successful? -> open human task: handle DSP error that expires in 90 days
expired? -> update status, END
retry case? -> goto validate DSP
dont retry case? -> update status, END

! ONBOARDING
open human task: customer onboarding completion
completed? -> update status, communicate about onboarding, wait for asset delivery initiation
not completed or expired in 6 months? -> update status, END

! ASSET DELIVERY
update status

! POD DOCUMENT CREATION
request to generate document
wait for callback
success? -> save output and continue
failure? -> raise snow incident

! POD SIGNING
double::
  ! POD DSP
  validate dsp
  all good? -> continue
  not good? -> open human task: handle invalid DSP
  when task handled -> goto validate dsp
  request to create DSP case
  double::
    wait for dsp callback
    subscribe to email notifications, END
  dsp successful? -> update status, communicate about agreement signed, continue
  dsp not successful? -> open human task: handle DSP error that expires in 90 days
  expired? -> update status, END
  retry case? -> goto validate DSP
  dont retry case? -> update status, END

  ! POD physical signing
  open human task for physical signing if opted for it
  
  when EITHER way pod is signed, cancel/close the other one if exists, continue
save output for all outcomes
update status

THE END

