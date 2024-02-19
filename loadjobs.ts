#!/usr/bin/env -S deno run --allow-net

await Promise.allSettled(
    Array.from(Array(1000)).map(async (_, i) => {
        console.log(`Sending request number ${i}`);
        await fetch('http://localhost:8080/jobs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ jobType: 'task-enqueue-test', jobData: {} }),
        });
        console.log(`Done sending request number ${i}`);
    }),
);
// To make this a module so we can use top level await
export {};