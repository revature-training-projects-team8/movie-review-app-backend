USE movies;

UPDATE users 
SET password = '$2a$10$MbVZUK2kd8HYdAHmuidzLeSfbV97oxp9oo3T04O8dP.zs1Ay6Cw4O'
WHERE username = 'admin';

SELECT 
    username, 
    email, 
    LEFT(password, 50) as password_hash, 
    role,
    CASE 
        WHEN password = '$2a$10$MbVZUK2kd8HYdAHmuidzLeSfbV97oxp9oo3T04O8dP.zs1Ay6Cw4O' 
        THEN '✅ Password updated successfully!' 
        ELSE '❌ Password not updated' 
    END as status
FROM users 
WHERE username = 'admin';
