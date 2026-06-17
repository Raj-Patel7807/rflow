export default function Pagination({
    page,
    totalPages,
    totalElements,
    size,
    onPageChange,
}) {
    if (totalElements === 0) return null;

    const start = page * size + 1;
    const end = Math.min((page + 1) * size, totalElements);

    return (
        <div className="pagination">
            <span className="pagination-info">
                Showing {start}-{end} of {totalElements}
            </span>

            <div className="pagination-controls">
                <button
                    type="button"
                    className="btn-secondary"
                    disabled={page <= 0}
                    onClick={() => onPageChange(page - 1)}
                >
                    Previous
                </button>

                <span className="pagination-page">
                    Page {page + 1} of {totalPages || 1}
                </span>

                <button
                    type="button"
                    className="btn-secondary"
                    disabled={page + 1 >= totalPages}
                    onClick={() => onPageChange(page + 1)}
                >
                    Next
                </button>
            </div>
        </div>
    );
}
