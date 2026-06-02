const CONFIG = {
    // Troque pelo WhatsApp oficial da empresa, usando DDI + DDD + número, somente dígitos.
    // Exemplo: 5585999999999
    whatsappNumber: "5585999999999",
    companyName: "Giramundo"
};

const products = [
    {
        id: 1,
        name: "Camiseta Eagle Road",
        description: "Camiseta premium com estampa frontal inspirada na águia Giramundo.",
        price: 89.9,
        badge: "01"
    },
    {
        id: 2,
        name: "Boné Rock Rider",
        description: "Boné preto com acabamento urbano e detalhe dourado para uso diário.",
        price: 69.9,
        badge: "02"
    },
    {
        id: 3,
        name: "Moletom World Tour",
        description: "Moletom encorpado para quem carrega atitude em qualquer estrada.",
        price: 189.9,
        badge: "03"
    },
    {
        id: 4,
        name: "Adesivo Giramundo Classic",
        description: "Kit de adesivos com estética rock, ideal para notebook, bike ou case.",
        price: 24.9,
        badge: "04"
    },
    {
        id: 5,
        name: "Caneca Backstage",
        description: "Caneca preta com visual backstage para café, oficina ou escritório.",
        price: 49.9,
        badge: "05"
    },
    {
        id: 6,
        name: "Ecobag Freedom Wings",
        description: "Bolsa resistente com visual dark e detalhe de asas em destaque.",
        price: 59.9,
        badge: "06"
    }
];

const state = {
    cart: JSON.parse(localStorage.getItem("giramundo_cart") || "[]")
};

const productGrid = document.getElementById("productGrid");
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
    return value.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

function saveCart() {
    localStorage.setItem("giramundo_cart", JSON.stringify(state.cart));
}

function renderProducts() {
    productGrid.innerHTML = products.map(product => `
        <article class="product-card">
            <div class="product-media" aria-hidden="true">
                <span>${product.badge}</span>
            </div>
            <div class="product-body">
                <h3>${product.name}</h3>
                <p>${product.description}</p>
                <div class="product-footer">
                    <strong class="price">${formatMoney(product.price)}</strong>
                    <button class="btn btn-primary" type="button" onclick="addToCart(${product.id})">
                        Adicionar
                    </button>
                </div>
            </div>
        </article>
    `).join("");
}

function addToCart(productId) {
    const product = products.find(item => item.id === productId);
    const existingItem = state.cart.find(item => item.id === productId);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        state.cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            quantity: 1
        });
    }

    saveCart();
    renderCart();
    openCart();
}

function updateQuantity(productId, operation) {
    const item = state.cart.find(cartItem => cartItem.id === productId);
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
    state.cart = state.cart.filter(item => item.id !== productId);
    saveCart();
    renderCart();
}

function clearCart() {
    state.cart = [];
    saveCart();
    renderCart();
}

function getCartTotal() {
    return state.cart.reduce((total, item) => total + item.price * item.quantity, 0);
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
                    <p>${formatMoney(item.price)} cada • Subtotal: ${formatMoney(item.price * item.quantity)}</p>
                </div>
                <div class="qty-control" aria-label="Controle de quantidade">
                    <button type="button" onclick="updateQuantity(${item.id}, 'decrease')">−</button>
                    <strong>${item.quantity}</strong>
                    <button type="button" onclick="updateQuantity(${item.id}, 'increase')">+</button>
                </div>
                <button class="remove-btn" type="button" onclick="removeFromCart(${item.id})">Remover item</button>
            </div>
        `).join("");
    }

    validateOrder();
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
        return `${index + 1}. ${item.name}\nQuantidade: ${item.quantity}\nValor unitário: ${formatMoney(item.price)}\nSubtotal: ${formatMoney(item.price * item.quantity)}`;
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
    cartDrawer.classList.add("open");
    cartDrawer.setAttribute("aria-hidden", "false");
}

function closeCart() {
    cartDrawer.classList.remove("open");
    cartDrawer.setAttribute("aria-hidden", "true");
}

function setupEvents() {
    openCartBtn.addEventListener("click", openCart);
    closeCartBtn.addEventListener("click", closeCart);
    clearCartBtn.addEventListener("click", clearCart);
    sendOrderBtn.addEventListener("click", sendOrderToWhatsapp);

    cartDrawer.addEventListener("click", event => {
        if (event.target === cartDrawer) closeCart();
    });

    [...addressFields, "complement"].forEach(id => {
        document.getElementById(id).addEventListener("input", validateOrder);
    });

    document.addEventListener("keydown", event => {
        if (event.key === "Escape") closeCart();
    });
}

function init() {
    displayWhatsapp.textContent = CONFIG.whatsappNumber;
    renderProducts();
    renderCart();
    setupEvents();
    setupFooter();
}

init();

function setupFooter() {
    const yearEl = document.getElementById('footerYear');
    if (yearEl) {
        yearEl.textContent = new Date().getFullYear();
    }

    const adminBtn = document.getElementById('adminPanelBtn');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            window.location.href = '/admin';
        });
    }
}
