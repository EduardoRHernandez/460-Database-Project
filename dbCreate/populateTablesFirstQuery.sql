@memberPopulate.sql         -- Members must exist first (referenced by LessonPurchase)
@employeePopulate.sql       -- Employees must exist before assigning as instructors
@instructorPopulate.sql     -- Instructors wrap employees
@lessonPopulate.sql         -- Lessons must exist (referencing instructors)
@lessonPurchase.sql         -- Purchases link members + lessons
@lessonLogPopulate.sql              -- Logs depend on existing purchases