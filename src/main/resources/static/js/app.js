const CONFIG = {
    // Troque pelo WhatsApp oficial da empresa, usando DDI + DDD + número, somente dígitos.
    // Exemplo: 5585999999999
    whatsappNumber: "5585999999999",
    companyName: "Giramundo"
};

const state = {
    cart: JSON.parse(localStorage.getItem("giramundo_cart") || "[]"),
    products: [],
    searchTerm: ""
};

const productGrid = document.getElementById("productGrid");
const productPagination = document.getElementById("productPagination");
const cartDrawer = document.getElementById("cartDrawer");
const cartItems = document.getElementById("cartItems");
const cartCounter = document.getElementById("cartCounter");
const cartTotal = document.getElementById("cartTotal");
const sendOrderBtn = document.getElementById("sendOrderBtn");
const clearCartBtn = document.getElementById("clearCartBtn");
const openCartBtn = document.getElementById("openCartBtn");
const closeCartBtn = document.getElementById("closeCartBtn");
const validationMessage = document.getElementById("cartValidationMessage");
const displayWhatsapp = document.getElementById("displayWhatsapp");
const mainNavLinks = document.querySelectorAll(".main-nav a");
const productSearch = document.getElementById("productSearch");
const eventsSearch = document.getElementById("eventsSearch");
const eventsGrid = document.getElementById("eventsGrid");
const eventsPagination = document.getElementById("eventsPagination");
const eventsEmpty = document.getElementById("eventsEmpty");
const shareEventBtn = document.getElementById("shareEventBtn");
const copyEventLinkBtn = document.getElementById("copyEventLinkBtn");
const shareWhatsApp = document.getElementById("shareWhatsApp");
const shareFacebook = document.getElementById("shareFacebook");
const shareX = document.getElementById("shareX");

const EVENTS_PER_PAGE = 6;
const PRODUCTS_PER_PAGE = 12;

const eventsState = {
    currentPage: 1
};

const productsState = {
    currentPage: 1
};

const addressFields = [
    "customerName",
    "customerPhone",
    "street",
    "number",
    "district",
    "city",
    "state"
];

function formatMoney(value) {
}

function normalizeText(value) {
    return String(value || "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");
}

function getProductImageUrl(product) {
    if (product.image) {
        return `/uploads/${product.image}`;
    }

    return null;
}

function renderProducts() {
    if (!productGrid) return;

    const filteredProducts = state.products.filter(product => {
        const query = normalizeText(state.searchTerm);
        if (!query) return true;

        const searchable = [product.name, product.description, product.price]
            .map(normalizeText)
            .join(" ");

        return searchable.includes(query);
    });

    if (filteredProducts.length === 0) {
        productGrid.innerHTML = `<div class="empty-cart product-empty">Nenhum produto encontrado para a pesquisa atual.</div>`;
        renderProductsPagination(0, 0);
        return;
    }

    const totalPages = Math.max(1, Math.ceil(filteredProducts.length / PRODUCTS_PER_PAGE));
    if (productsState.currentPage > totalPages) {
        productsState.currentPage = totalPages;
    }

    const startIndex = (productsState.currentPage - 1) * PRODUCTS_PER_PAGE;
    const endIndex = startIndex + PRODUCTS_PER_PAGE;
    const pageProducts = filteredProducts.slice(startIndex, endIndex);

    productGrid.innerHTML = pageProducts.map(product => {
        const imageUrl = getProductImageUrl(product);
        const price = Number(product.price || 0);
        const quantity = product.quantity ?? 0;

        return `
        <article class="product-card">
            <div class="product-media ${imageUrl ? "has-image" : ""}" aria-hidden="true">
                ${imageUrl ? `<img src="${imageUrl}" alt="${product.name}">` : `<span>${String(product.name || "").slice(0, 2).toUpperCase()}</span>`}
            </div>
            <div class="product-body">
                <div class="product-meta">
                    <strong class="product-stock">${quantity > 0 ? `${quantity} em estoque` : "Sob consulta"}</strong>
                </div>
                <h3>${product.name}</h3>
                <p>${product.description || "Produto disponível no painel admin."}</p>
                <div class="product-footer">
                    <strong class="price">${formatMoney(price)}</strong>
                    <button class="btn btn-primary" type="button" data-product-id="${product.id}">
                        Comprar
                    </button>
                </div>
            </div>
        </article>
    `;
    }).join("");
}

function renderEventsFilter() {
    if (!eventsGrid) return;

    const query = normalizeText(eventsSearch ? eventsSearch.value : "");
    const cards = eventsGrid.querySelectorAll(".event-card");
    const visibleCards = [];

    cards.forEach(card => {
        const searchable = normalizeText(card.dataset.search || card.textContent || "");
        const isVisible = !query || searchable.includes(query);

        if (isVisible) {
            visibleCards.push(card);
        }
    });

    const totalPages = Math.max(1, Math.ceil(visibleCards.length / EVENTS_PER_PAGE));
    if (eventsState.currentPage > totalPages) {
        eventsState.currentPage = totalPages;
    }

    cards.forEach(card => {
        card.classList.add("is-hidden");
    });

    const startIndex = (eventsState.currentPage - 1) * EVENTS_PER_PAGE;
    const endIndex = startIndex + EVENTS_PER_PAGE;

    visibleCards.slice(startIndex, endIndex).forEach(card => {
        card.classList.remove("is-hidden");
    });

    if (eventsEmpty) {
        eventsEmpty.hidden = visibleCards.length !== 0;
    }

    renderEventsPagination(totalPages, visibleCards.length);
}

function renderEventsPagination(totalPages, totalItems) {
    if (!eventsPagination) return;

    if (totalItems === 0 || totalPages <= 1) {
        eventsPagination.innerHTML = "";
        eventsPagination.hidden = true;
        return;
    }

    eventsPagination.hidden = false;

    const previousDisabled = eventsState.currentPage === 1 ? "disabled" : "";
    const nextDisabled = eventsState.currentPage === totalPages ? "disabled" : "";

    eventsPagination.innerHTML = `
        <button type="button" class="btn btn-secondary btn-small" data-pagination-action="prev" ${previousDisabled}>
            Anterior
        </button>
        <span class="events-pagination-info">Página ${eventsState.currentPage} de ${totalPages}</span>
        <button type="button" class="btn btn-secondary btn-small" data-pagination-action="next" ${nextDisabled}>
            Próxima
        </button>
    `;
}

function getEventShareData() {
    return window.__eventShareData || null;
}

function buildShareText() {
    const data = getEventShareData();
    if (!data) return "";

    return `${data.title}\n\n${data.description}\n\n${data.url}`;
}

function setupEventSharing() {
    const data = getEventShareData();
    if (!data) return;

    const text = `${data.title} - ${data.description}`;
    const encodedText = encodeURIComponent(text);
    const encodedUrl = encodeURIComponent(data.url);

    if (shareWhatsApp) {
        shareWhatsApp.href = `https://wa.me/?text=${encodeURIComponent(buildShareText())}`;
    }

    if (shareFacebook) {
        shareFacebook.href = `https://www.facebook.com/sharer/sharer.php?u=${encodedUrl}`;
    }

    if (shareX) {
        shareX.href = `https://twitter.com/intent/tweet?text=${encodedText}%20${encodedUrl}`;
    }

    if (shareEventBtn) {
        shareEventBtn.addEventListener("click", async () => {
            const sharePayload = {
                title: data.title,
                text: text,
                url: data.url
            };

            if (navigator.share) {
                try {
                    await navigator.share(sharePayload);
                    return;
                } catch (error) {
                    // fallback abaixo
                }
            }

            try {
                await navigator.clipboard.writeText(buildShareText());
                alert("Link da publicação copiado para a área de transferência.");
            } catch (error) {
                window.prompt("Copie o link da publicação", data.url);
            }
        });
    }

    if (copyEventLinkBtn) {
        copyEventLinkBtn.addEventListener("click", async () => {
            try {
                await navigator.clipboard.writeText(data.url);
                copyEventLinkBtn.textContent = "Link copiado";
                setTimeout(() => {
                    copyEventLinkBtn.textContent = "Copiar link";
                }, 1800);
            } catch (error) {
                window.prompt("Copie o link da publicação", data.url);
            }
        });
    }
}

function addToCart(productId) {
    const product = state.products.find(item => String(item.id) === String(productId));
    if (!product) return;
    const existingItem = state.cart.find(item => item.id === productId);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        state.cart.push({
            id: String(product.id),
            name: product.name,
            price: Number(product.price || 0),
            image: product.image || "",
            quantity: 1
        });
    }

    saveCart();
    renderCart();
    openCart();
}

function updateQuantity(productId, operation) {
    const item = state.cart.find(cartItem => String(cartItem.id) === String(productId));
    if (!item) return;

    if (operation === "increase") {
        item.quantity += 1;
    }

    if (operation === "decrease") {
        item.quantity -= 1;
    }

    if (item.quantity <= 0) {
        removeFromCart(productId);
        return;
    }

    saveCart();
    renderCart();
}

function removeFromCart(productId) {
    state.cart = state.cart.filter(item => String(item.id) !== String(productId));
    saveCart();
    renderCart();
}

function clearCart() {
    state.cart = [];
    saveCart();
    renderCart();
}

function getCartTotal() {
    return state.cart.reduce((total, item) => total + Number(item.price || 0) * item.quantity, 0);
}

function getCartCount() {
    return state.cart.reduce((total, item) => total + item.quantity, 0);
}

function renderCart() {
    cartCounter.textContent = getCartCount();
    cartTotal.textContent = formatMoney(getCartTotal());

    if (state.cart.length === 0) {
        cartItems.innerHTML = `<div class="empty-cart">Seu carrinho está vazio.</div>`;
    } else {
        cartItems.innerHTML = state.cart.map(item => `
            <div class="cart-item">
                <div>
                    <h4>${item.name}</h4>
                    <p>${formatMoney(Number(item.price || 0))} cada • Subtotal: ${formatMoney(Number(item.price || 0) * item.quantity)}</p>
                </div>
                <div class="qty-control" aria-label="Controle de quantidade">
                    <button type="button" onclick="updateQuantity('${item.id}', 'decrease')">−</button>
                    <strong>${item.quantity}</strong>
                    <button type="button" onclick="updateQuantity('${item.id}', 'increase')">+</button>
                </div>
                <button class="remove-btn" type="button" onclick="removeFromCart('${item.id}')">Remover item</button>
            </div>
        `).join("");
    }

    validateOrder();
}

function renderProductsPagination(totalPages, totalItems) {
    if (!productPagination) return;

    if (totalItems === 0 || totalPages <= 1) {
        productPagination.innerHTML = "";
        productPagination.hidden = true;
        return;
    }

    productPagination.hidden = false;

    const previousDisabled = productsState.currentPage === 1 ? "disabled" : "";
    const nextDisabled = productsState.currentPage === totalPages ? "disabled" : "";

    productPagination.innerHTML = `
        <button type="button" class="btn btn-secondary btn-small" data-product-pagination-action="prev" ${previousDisabled}>
            Anterior
        </button>
        <span class="events-pagination-info">Página ${productsState.currentPage} de ${totalPages}</span>
        <button type="button" class="btn btn-secondary btn-small" data-product-pagination-action="next" ${nextDisabled}>
            Próxima
        </button>
    `;
}

function getFieldValue(id) {
    return document.getElementById(id).value.trim();
}

function isAddressComplete() {
    return addressFields.every(id => getFieldValue(id).length > 0);
}

function validateOrder() {
    const hasProducts = state.cart.length > 0;
    const hasAddress = isAddressComplete();
    sendOrderBtn.disabled = !(hasProducts && hasAddress);

    if (!hasProducts) {
        validationMessage.textContent = "Adicione pelo menos um produto para solicitar o pedido.";
        return;
    }

    if (!hasAddress) {
        validationMessage.textContent = "Preencha os campos obrigatórios do endereço para liberar o pedido.";
        return;
    }

    validationMessage.textContent = "Pedido pronto para envio via WhatsApp.";
}

function buildOrderMessage() {
    const customer = {
        name: getFieldValue("customerName"),
        phone: getFieldValue("customerPhone"),
        street: getFieldValue("street"),
        number: getFieldValue("number"),
        district: getFieldValue("district"),
        city: getFieldValue("city"),
        state: getFieldValue("state").toUpperCase(),
        complement: getFieldValue("complement")
    };

    const productLines = state.cart.map((item, index) => {
        return `${index + 1}. ${item.name}\nQuantidade: ${item.quantity}\nValor unitário: ${formatMoney(Number(item.price || 0))}\nSubtotal: ${formatMoney(Number(item.price || 0) * item.quantity)}`;
    }).join("\n\n");

    const address = `${customer.street}, ${customer.number} - ${customer.district}, ${customer.city}/${customer.state}`;
    const complement = customer.complement ? `\nComplemento/Referência: ${customer.complement}` : "";

    return `Olá, ${CONFIG.companyName}! Quero solicitar um pedido.\n\n` +
        `*Dados do cliente*\n` +
        `Nome: ${customer.name}\n` +
        `WhatsApp: ${customer.phone}\n\n` +
        `*Itens do pedido*\n${productLines}\n\n` +
        `*Total:* ${formatMoney(getCartTotal())}\n\n` +
        `*Endereço de entrega*\n${address}${complement}`;
}

function sendOrderToWhatsapp() {
    if (sendOrderBtn.disabled) return;

    const message = encodeURIComponent(buildOrderMessage());
    const url = `https://wa.me/${CONFIG.whatsappNumber}?text=${message}`;
    window.open(url, "_blank", "noopener,noreferrer");
}

function openCart() {
    if (typeof cartDrawer.showModal === "function" && !cartDrawer.open) {
        cartDrawer.showModal();
    }

    cartDrawer.classList.add("open");
    cartDrawer.setAttribute("aria-hidden", "false");
}

function bindGlobalCartControls() {
    if (openCartBtn) {
        openCartBtn.addEventListener("click", openCart);
    }

    if (closeCartBtn) {
        closeCartBtn.addEventListener("click", closeCart);
    }

    if (cartDrawer) {
        cartDrawer.addEventListener("click", event => {
            if (event.target === cartDrawer) closeCart();
        });
    }
}

function closeCart() {
    cartDrawer.classList.remove("open");
    cartDrawer.setAttribute("aria-hidden", "true");

    if (cartDrawer.open) {
        cartDrawer.close();
    }
}

function setupEvents() {
    clearCartBtn.addEventListener("click", clearCart);
    sendOrderBtn.addEventListener("click", sendOrderToWhatsapp);

    [...addressFields, "complement"].forEach(id => {
        document.getElementById(id).addEventListener("input", validateOrder);
    });

    if (productSearch) {
        productSearch.addEventListener("input", event => {
            state.searchTerm = event.target.value;
            productsState.currentPage = 1;
            renderProducts();
        });
    }

    if (productPagination) {
        productPagination.addEventListener("click", event => {
            const button = event.target.closest("button[data-product-pagination-action]");
            if (!button || button.disabled) return;

            const query = normalizeText(state.searchTerm);
            const filteredProducts = state.products.filter(product => {
                if (!query) return true;

                const searchable = [product.name, product.description, product.price]
                    .map(normalizeText)
                    .join(" ");

                return searchable.includes(query);
            });

            const totalPages = Math.max(1, Math.ceil(filteredProducts.length / PRODUCTS_PER_PAGE));

            if (button.dataset.productPaginationAction === "prev") {
                productsState.currentPage = Math.max(1, productsState.currentPage - 1);
            }

            if (button.dataset.productPaginationAction === "next") {
                productsState.currentPage = Math.min(totalPages, productsState.currentPage + 1);
            }

            renderProducts();
            productGrid.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    }

    if (eventsSearch) {
        eventsSearch.addEventListener("input", () => {
            eventsState.currentPage = 1;
            renderEventsFilter();
        });
    }

    if (eventsPagination) {
        eventsPagination.addEventListener("click", event => {
            const button = event.target.closest("button[data-pagination-action]");
            if (!button || button.disabled) return;

            if (button.dataset.paginationAction === "prev") {
                eventsState.currentPage = Math.max(1, eventsState.currentPage - 1);
            }

            if (button.dataset.paginationAction === "next") {
                const visibleCards = [...eventsGrid.querySelectorAll(".event-card")].filter(card => {
                    const searchable = normalizeText(card.dataset.search || card.textContent || "");
                    const query = normalizeText(eventsSearch ? eventsSearch.value : "");
                    return !query || searchable.includes(query);
                });
                const totalPages = Math.max(1, Math.ceil(visibleCards.length / EVENTS_PER_PAGE));
                eventsState.currentPage = Math.min(totalPages, eventsState.currentPage + 1);
            }

            renderEventsFilter();
            eventsGrid.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    }

    if (productGrid) {
        productGrid.addEventListener("click", event => {
            const button = event.target.closest("button[data-product-id]");
            if (!button) return;

            addToCart(button.dataset.productId);
        });
    }

    document.addEventListener("keydown", event => {
        if (event.key === "Escape") closeCart();
    });
}

async function loadProducts() {
    if (!productGrid) return;

    try {
        const response = await fetch("/api/products", { headers: { Accept: "application/json" } });
        if (!response.ok) {
            throw new Error("Falha ao carregar produtos");
        }

        state.products = await response.json();
    } catch (error) {
        state.products = [];
        productGrid.innerHTML = `<div class="empty-cart product-empty">Não foi possível carregar os produtos do painel admin.</div>`;
        return;
    }

    renderProducts();
    updateActiveNavLink();
}

function init() {
    setupFooter();
    bindGlobalCartControls();

    if (cartDrawer && cartItems && cartCounter && cartTotal && sendOrderBtn && clearCartBtn && openCartBtn && closeCartBtn && validationMessage) {
        setupEvents();
        renderCart();
    }

    updateActiveNavLink();
    setupEventSharing();

    if (displayWhatsapp) {
        displayWhatsapp.textContent = CONFIG.whatsappNumber;
    }

    loadProducts();
    renderEventsFilter();

    window.addEventListener("hashchange", updateActiveNavLink);
    window.addEventListener("popstate", updateActiveNavLink);
}

init();

function setupFooter() {
    const yearEl = document.getElementById('footerYear');
    if (yearEl) {
        yearEl.textContent = new Date().getFullYear();
    }
}

function getCurrentNavTarget() {
    const pathname = window.location.pathname.replace(/\/+$/, "") || "/";

    if (pathname === "/loja" || pathname.startsWith("/loja/")) {
        return "/loja";
    }

    if (pathname === "/sobre" || pathname.startsWith("/sobre/")) {
        return "/sobre";
    }

    if (pathname === "/eventos" || pathname.startsWith("/eventos/")) {
        return "/eventos";
    }

    const hash = window.location.hash || "#home";
    return `/${hash}`;
}

function updateActiveNavLink() {
    const currentTarget = getCurrentNavTarget();
    const links = document.querySelectorAll(".main-nav a");

    links.forEach(link => {
        const linkTarget = link.getAttribute("href") || "";
        const isActive = linkTarget === currentTarget;
        link.classList.toggle("is-active", isActive);

        if (isActive) {
            link.setAttribute("aria-current", "page");
        } else {
            link.removeAttribute("aria-current");
        }
    });
}
