# Backlog

Well-scoped tickets for the digital employee. Each is small, testable, and safe — the kind of work a
junior engineer picks up. File any of these as a Jira ticket and assign it to the employee.

## Story: Delete a widget
Add `DELETE /widgets/{id}` — remove a widget by id, returning `204 No Content`; `404` if it doesn't
exist. Add a `WidgetService.delete(id)` and cover both cases with tests.
**Acceptance:** DELETE removes the widget; a follow-up GET returns 404; deleting an unknown id returns 404.

## Story: Validate widget creation
Reject invalid `POST /widgets` bodies: `name` must be non-blank, `quantity` must be `>= 0`. Return
`400 Bad Request` with a helpful message. Use Bean Validation on the request record.
**Acceptance:** blank name → 400; negative quantity → 400; a valid body still returns 200.

## Story: Search widgets by name
Add `GET /widgets?name=<substring>` — case-insensitive contains filter over the name; no param returns
all. Cover the filter with tests.
**Acceptance:** `?name=bo` returns only matching widgets; no param returns everything.

## Bug: Quantity accepts negatives silently
`create()` stores negative quantities without complaint. Guard it (or fold into the validation story)
so negative quantities can't be created.
**Acceptance:** a test proves a negative quantity is rejected, not stored.

## Chore: Add a service-layer test for update
(Once an update endpoint exists) — not yet applicable; placeholder for the next iteration.
