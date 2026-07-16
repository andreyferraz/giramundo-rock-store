# Destaque da página inicial com imagem da loja

## Objetivo

Substituir o card de destaque que atualmente mostra a logo e dois textos por uma foto da vitrine da loja.

## Escopo

- Alterar apenas o card visual da seção `hero` em `src/main/resources/templates/index.html`.
- Usar `img/home.png` como fonte da nova imagem.
- Remover a marcação textual associada à antiga logo (`GIRAMUNDO ORIGINAL` e `Loja pronta para backend Spring`).
- Preservar o conteúdo textual e os botões à esquerda da seção inicial.

## Comportamento visual

- O card mantém seu lugar no layout atual, mas passa a conter somente a imagem.
- A imagem ocupa o card integralmente com enquadramento central e recorte proporcional (`object-fit: cover`).
- O tamanho deve acompanhar os estilos responsivos já existentes para o card, sem criar transbordamento em telas menores.
- Bordas, sombra e paleta permanecem coerentes com a estética escura da página.

## Validação

- Confirmar que não há referência à logo antiga nem aos textos removidos no card.
- Verificar que o template Maven continua compilando e que a imagem é referenciada pelo caminho estático correto.
