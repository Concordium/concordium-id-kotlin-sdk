package com.concordium.sdk.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.concordium.sdk.R
import com.concordium.sdk.common.toDp
import com.concordium.sdk.common.toPixels
import com.concordium.sdk.ui.model.AccountAction
import com.concordium.sdk.ui.model.StepItem
import com.concordium.sdk.ui.theme.Black
import com.concordium.sdk.ui.theme.Blue
import com.concordium.sdk.ui.theme.Grayish
import com.concordium.sdk.ui.theme.Typography
import com.concordium.sdk.ui.theme.White

@Composable
internal fun HeaderSection(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.image_logo),
            contentDescription = stringResource(R.string.company_name),
            modifier = Modifier
                .padding(top = 32.dp)
                .height(32.dp)
                .align(Alignment.Center)
        )
        IconButton(
            onClick = onClose, modifier = Modifier
                .padding(
                    top = 8.dp,
                    end = 8.dp,
                )
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "close button",
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
internal fun AccountSetupSection(instruction: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .padding(
                horizontal = 32.dp,
                vertical = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = instruction,
            style = Typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue,
                contentColor = Color.White
            ),
            onClick = {}) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                text = "Open {IDApp}",
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}

@Composable
fun QRCodeSection(deepLinkInvoke: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 32.dp,
                vertical = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = stringResource(R.string.message_complete_setup_in_id_App),
            style = Typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier
                .size(200.dp)
                .background(Grayish)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue,
                contentColor = Color.White
            ),
            onClick = {}) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                text = "Open {IDApp}",
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}

@Composable
fun IdVerificationSection(
    accountAction: AccountAction,
    onCreate: () -> Unit,
    onRecover: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 32.dp,
                vertical = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = stringResource(R.string.message_only_after_id_verification),
            style = Typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        when (accountAction) {
            AccountAction.Recover -> {
                CtaContainer(
                    ctaText = stringResource(R.string.recover),
                    onClick = onRecover
                )
            }

            is AccountAction.Create -> {
                CtaContainer(
                    ctaText = stringResource(R.string.create_new_account),
                    onClick = onCreate
                )
            }

            else -> {
                Column {
                    CtaContainer(
                        ctaText = stringResource(R.string.create_new_account),
                        onClick = onCreate
                    )
                    Spacer(Modifier.height(16.dp))
                    RecoverCta(
                        ctaText = stringResource(R.string.recover),
                        onClick = onRecover
                    )
                }
            }
        }

    }
}

@Composable
private fun CtaContainer(ctaText: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Blue,
            contentColor = Color.White
        ),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = ctaText,
            style = Typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Composable
private fun RecoverCta(ctaText: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Black
        ),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = ctaText,
            style = Typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Composable
internal fun StepperView(
    items: List<StepItem>,
    modifier: Modifier = Modifier,
    lineColor: Color = Black,
    filledColor: Color = Blue,
    radiusInDp: Dp = 8.dp,
    gapInDp: Dp = 90.dp,
) {
    val gap = gapInDp.toPixels()
    val width = gap * items.size

    val radius = radiusInDp.toPixels()
    val height = radius + 10f * 2
    val strokeWidth = 1.dp.toPixels()

    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(width = width.toDp(), height = height.toDp())) {
            var offsetX = -gap / 2
            val offsetY = radius + 10f

            repeat(items.size) {
                val currItem = items[it]
                offsetX += gap

                val color = if (currItem.selected) filledColor else lineColor
                val style = if (currItem.selected) {
                    Fill
                } else {
                    Stroke(
                        width = strokeWidth,
                    )
                }
                val circleRadius = if (currItem.selected) radius else (radius - strokeWidth)

                drawCircle(
                    color = color,
                    radius = circleRadius,
                    center = Offset(x = offsetX, y = offsetY),
                    style = style,
                )
                if (it > 0) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x = offsetX - gap + radius - 0.5f, y = offsetY),
                        end = Offset(x = offsetX - radius + 0.5f, y = offsetY),
                        strokeWidth = strokeWidth,
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.size(width = width.toDp(), height = Dp.Infinity),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            repeat(items.size) {
                val currItem = items[it]
                Text(
                    modifier = Modifier
                        .widthIn(max = gapInDp - (2 * radiusInDp)),
                    text = currItem.label,
                    style = Typography.bodySmall.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = if (currItem.selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (currItem.selected) Black else Black.copy(alpha = 0.7f)
                    ),
                )
            }
        }
    }
}

@Composable
fun MatchCodeSection(instruction: String, codeText: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Grayish)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = instruction,
            style = Typography.displayMedium,
            textAlign = TextAlign.Center,
        )
        CircularBox(text = codeText)
    }
}

@Composable
private fun CircularBox(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    strokeWidth: Dp = 3.dp,
    strokeColor: Color = Blue,
    textStyle: TextStyle = Typography.displayLarge
) {
    Box(
        modifier = modifier
            .size(size)
            .aspectRatio(1f)
            .border(
                width = strokeWidth,
                color = strokeColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center,
            color = Blue,
        )
    }
}

@Composable
internal fun PlayStoreSection(
    infoText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Grayish)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = infoText,
            style = Typography.displayMedium,
            textAlign = TextAlign.Center,
        )
        Image(
            modifier = Modifier.height(38.dp),
            painter = painterResource(R.drawable.play_store_icon),
            contentDescription = infoText,
        )
    }
}

@Preview
@Composable
internal fun SdkPopUpPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderSection(onClose = {})

        StepperView(
            items = listOf(
                StepItem("Step 1", true),
                StepItem("Step 2", false),
                StepItem("Step 3", false),
            )
        )
        PlayStoreSection(infoText = stringResource(R.string.info_text_play_store))
        MatchCodeSection(
            codeText = "1236",
            instruction = stringResource(R.string.message_match_code_in_IDapp)
        )
        AccountSetupSection(instruction = stringResource(R.string.message_match_code_in_IDapp))

        RecoverCta(ctaText = "Recover", onClick = {})
    }
}