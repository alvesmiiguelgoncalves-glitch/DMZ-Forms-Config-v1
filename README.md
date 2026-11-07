# DMZ Form Options

Addon para o **DragonMine Z 2.2** (Forge 1.20.1) que acrescenta opções novas aos JSON das transformações
(`config/dragonminez/races/<raça>/forms/<grupo>.json`, dentro de cada forma). Os nomes das opções estão em inglês, como os do DMZ.
Todas têm um valor padrão que não muda nada: só quem escreve a opção no JSON vê efeito.

Precisa do addon no cliente (e no servidor, se jogar em servidor, para a config sincronizar).

## Opções

| Opção | Tipo / padrão | O que faz |
|---|---|---|
| `forceTiedTail` | boolean / `false` | A cauda fica amarrada na cintura nessa forma (útil no SSJ3, em que a cauda some dentro do cabelo). |
| `forceLooseTail` | boolean / `false` | A cauda fica solta nessa forma (útil no SSJ4). Se as duas estiverem `true`, a solta vence. |
| `stackTintAffectsHairAndTail` | boolean / `true` | Vale na forma **base** (ex.: Blue). Com `false`, a tinta de uma forma empilhável (Kaio-ken) **não** pinta cabelo e cauda. |
| `stackTintHairAndTailIntensity` | número / `-1` | Vale na forma **base**. De 0 a 1: intensidade da tinta da forma empilhável **no cabelo e na cauda**. `-1` = usa a intensidade da própria forma empilhável. |
| `scleraColor` | texto / `""` | Cor da esclera (parte branca do olho), por exemplo `"#FFFFFF"`. Vazio = cor padrão do jogo. |
| `noEyebrows` | boolean / `false` | Esconde as sobrancelhas (usa a mesma textura `ssj3eyebrows_eye_N` que o DMZ usa no SSJ3, escolhida pelo tipo de olho do jogador). |
| `eyeliner` | boolean / `false` | Desenha o delineado nos olhos (usa a textura `ssj4_eyes_N` do DMZ, escolhida pelo tipo de olho do jogador). |
| `eyelinerColor` | texto / `"#000000"` | Cor do delineado. |
| `chargeKiAnimation` | boolean / `true` | Com `false`, o jogador **não** faz a pose de carregar ki: fica parado (idle) enquanto a aura carrega. |

## Exemplo

```json
"supersaiyan3": {
  "forceTiedTail": true,
  "noEyebrows": true
},
"supersaiyan4": {
  "forceLooseTail": true,
  "eyeliner": true,
  "eyelinerColor": "#B00000"
},
"supersaiyanblue": {
  "stackTintAffectsHairAndTail": true,
  "stackTintHairAndTailIntensity": 0.1
},
"masteredultrainstinct": {
  "chargeKiAnimation": false,
  "scleraColor": "#FFFFFF"
}
```

(O nome da forma é a chave do objeto dentro de `"forms"`; coloque as opções junto das outras, por exemplo perto de `"hideTail"`.)

Depois de salvar, use `/dmzreload config`.

## Observações

- **Ordem no arquivo:** se o DMZ regravar o arquivo (atualização de versão), as opções novas vão para o fim de cada forma, não ao lado do `hideTail`.
- **Olhos:** esclera, sobrancelha e delineado valem para o rosto de estilo humano/saiyajin. Raças com rosto próprio (Namekuseijin, Freeza etc.) não usam essas opções.
- **Delineado:** só aparece se existir `ssj4_eyes_N.png` para o tipo de olho do jogador (o DMZ já traz as do SSJ4).
- **Tinta do Kaio-ken:** a opção vai na forma **base**. Afeta o cabelo, a cauda desenhada na camada do corpo e as partes de raça (cauda, antenas). Sobrancelhas e olhos continuam com a tinta normal.
- **Mixins:** o addon altera classes do DMZ 2.2. Se uma opção não fizer nada, veja o `logs/latest.log` e procure por `dmz_form_options` (o jogo abre mesmo se algum mixin falhar).
- Não foi testado dentro do jogo.
