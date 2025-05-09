function dequeueBooking() {
    fetch('/api/queue/dequeue', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => {
        if (!response.ok) throw new Error('Dequeue failed');
        return response.json();
    })
    .then(data => {
        alert(`Dequeued booking: ${data.id}`);
        location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to dequeue booking.');
    });
}