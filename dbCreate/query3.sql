SELECT 
    t.name AS trail_name,
    t.category,
    l.name AS lift_name
FROM 
    Trail t
JOIN 
    Lift l ON t.name = l.name
WHERE 
    t.difficulty = 'intermediate'
    AND t.status = 'open'
    AND l.status = 'open'
ORDER BY 
    t.name;
