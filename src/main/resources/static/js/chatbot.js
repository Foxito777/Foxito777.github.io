/**
 * CHATBOT CON GEMINI AI - JULEDTOYS
 * Sistema de chat inteligente integrado con Google Gemini
 */

class Chatbot {
    constructor() {
        this.conversacionId = null;
        this.init();
    }

    init() {
        // Crear elementos del chatbot
        this.crearElementos();
        
        // Event listeners
        document.getElementById('chatbot-button').addEventListener('click', () => this.toggleChat());
        document.getElementById('chatbot-close').addEventListener('click', () => this.toggleChat());
        document.getElementById('chatbot-send').addEventListener('click', () => this.enviarMensaje());
        
        const input = document.getElementById('chatbot-input');
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.enviarMensaje();
            }
        });
        
        // Prevenir comportamiento extraño con espacios
        input.addEventListener('keydown', (e) => {
            // Permitir espacios normalmente
            if (e.key === ' ' || e.code === 'Space') {
                e.stopPropagation();
            }
        });

        // Mensaje de bienvenida
        this.mostrarMensajeBienvenida();
    }

    crearElementos() {
        const html = `
            <!-- Botón flotante -->
            <button id="chatbot-button" title="Chat con IA">
                <img src="Imagenes/chatbot.jpg" alt="Chatbot" style="width: 100%; height: 100%; object-fit: cover; border-radius: 12px;" />
            </button>

            <!-- Ventana del chat -->
            <div id="chatbot-container">
                <div id="chatbot-header">
                    <h3>💬 Asistente Juledtoys</h3>
                    <button id="chatbot-close">×</button>
                </div>
                
                <div id="chatbot-messages">
                    <!-- Los mensajes se agregan aquí dinámicamente -->
                </div>
                
                <div class="chatbot-typing">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
                
                <div id="chatbot-input-area">
                    <input 
                        type="text" 
                        id="chatbot-input" 
                        placeholder="Escribe tu pregunta..." 
                        autocomplete="off"
                        spellcheck="false"
                    />
                    <button id="chatbot-send">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                        </svg>
                    </button>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', html);
    }

    toggleChat() {
        const container = document.getElementById('chatbot-container');
        container.classList.toggle('open');
        
        if (container.classList.contains('open')) {
            document.getElementById('chatbot-input').focus();
        }
    }

    mostrarMensajeBienvenida() {
        const mensajesDiv = document.getElementById('chatbot-messages');
        const welcomeHtml = `
            <div class="chatbot-welcome">
                <strong>¡Hola! 👋</strong>
                Soy el asistente virtual de <strong style="color: #ff6b35;">Juledtoys</strong>.<br>
                ¿En qué puedo ayudarte hoy?
            </div>
        `;
        mensajesDiv.innerHTML = welcomeHtml;
    }

    agregarMensaje(texto, esUsuario = false) {
        const mensajesDiv = document.getElementById('chatbot-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = `chatbot-message ${esUsuario ? 'user' : 'bot'}`;
        messageDiv.textContent = texto;
        
        mensajesDiv.appendChild(messageDiv);
        
        // Scroll automático al último mensaje
        mensajesDiv.scrollTop = mensajesDiv.scrollHeight;
    }

    mostrarEscribiendo(mostrar = true) {
        const typingDiv = document.querySelector('.chatbot-typing');
        if (mostrar) {
            typingDiv.classList.add('active');
        } else {
            typingDiv.classList.remove('active');
        }
        
        // Scroll al final
        const mensajesDiv = document.getElementById('chatbot-messages');
        mensajesDiv.scrollTop = mensajesDiv.scrollHeight;
    }

    async enviarMensaje() {
        const input = document.getElementById('chatbot-input');
        const mensaje = input.value.trim();
        
        if (!mensaje) return;
        
        // Mostrar mensaje del usuario
        this.agregarMensaje(mensaje, true);
        input.value = '';
        
        // Deshabilitar input mientras se procesa
        const sendButton = document.getElementById('chatbot-send');
        input.disabled = true;
        sendButton.disabled = true;
        
        // Mostrar indicador de "escribiendo..."
        this.mostrarEscribiendo(true);
        
        try {
            // Llamar al backend
            console.log('🔄 Enviando mensaje al servidor:', mensaje);
            
            const response = await fetch('/api/chatbot/mensaje', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    mensaje: mensaje,
                    conversacionId: this.conversacionId
                })
            });
            
            console.log('📡 Respuesta del servidor - Status:', response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('❌ Error del servidor:', errorText);
                throw new Error('Error en la respuesta del servidor');
            }
            
            const data = await response.json();
            console.log('✅ Datos recibidos:', data);
            
            // Guardar ID de conversación
            if (data.conversacionId) {
                this.conversacionId = data.conversacionId;
            }
            
            // Ocultar indicador de "escribiendo..."
            this.mostrarEscribiendo(false);
            
            // Mostrar respuesta del bot
            if (data.exito && data.respuesta) {
                this.agregarMensaje(data.respuesta, false);
            } else {
                this.agregarMensaje('Lo siento, ocurrió un error. Por favor, intenta nuevamente.', false);
            }
            
        } catch (error) {
            console.error('Error al enviar mensaje:', error);
            this.mostrarEscribiendo(false);
            this.agregarMensaje('Error de conexión. Por favor, verifica tu internet e intenta nuevamente.', false);
        } finally {
            // Rehabilitar input
            input.disabled = false;
            sendButton.disabled = false;
            input.focus();
        }
    }
}

// Inicializar chatbot cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => new Chatbot());
} else {
    new Chatbot();
}
