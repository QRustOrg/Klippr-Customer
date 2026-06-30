package com.example.klippr.promotions.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.promotions.domain.model.PromotionCategory
import com.example.klippr.shared.presentation.theme.KlipprTextGray
import java.util.UUID

// @author Samuel Bonifacio

private val KlipprPurple = Color(0xFF887BF3)
private val KlipprLavender = Color(0xFFF0D8FF)
private val DashColor = Color(0xFFCCAEFF)
private val ErrorRed = Color(0xFFE53935)
private val TextGray = KlipprTextGray

// ── Local types ──────────────────────────────────────────────────────────────

private data class CreateFormState(
    val title: String = "",
    val description: String = "",
    val discountValue: String = "",
    val category: PromotionCategory = PromotionCategory.OTHER,
    val endDate: String = "",
    val conditions: List<ConditionItem> = emptyList(),
    val qrCode: String = generateQrCode(),
    val titleError: Boolean = false,
    val descriptionError: Boolean = false,
)

private data class ConditionItem(
    val id: String = UUID.randomUUID().toString(),
    val type: ConditionType? = null,
    val value: String = "",
    val dropdownExpanded: Boolean = false,
)

private enum class ConditionType(val label: String) {
    USAGE_LIMIT("Límite total de usos"),
    MIN_PURCHASE("Monto Mínimo de Compra"),
    VALIDATION_HOURS("Horario de validación"),
    VALID_DAYS("Días de la semana válidos"),
    VALID_BRANCHES("Sucursales válidas"),
    NEW_CLIENTS("Nuevos clientes"),
}

private fun generateQrCode(): String {
    val hex = "0123456789ABCDEFabcdef"
    return "PROM" + (1..12).map { hex.random() }.joinToString("")
}

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePromotionScreen(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var form by remember { mutableStateOf(CreateFormState()) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("+ QR", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KlipprPurple),
            )
        },
        bottomBar = {
            CreateBottomBar(onNavigateToHome = onNavigateToHome)
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // ── A. Información de la Promoción ────────────────────────────────
            item {
                SectionHeader(title = "Información de la Promocion")
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Título
                    FieldLabel("Titulo de la Promoción *", isError = form.titleError)
                    OutlinedTextField(
                        value = form.title,
                        onValueChange = { form = form.copy(title = it, titleError = false) },
                        placeholder = { Text("Ej: 2x1 en todas las pizzas", color = TextGray, fontSize = 14.sp) },
                        singleLine = true,
                        isError = form.titleError,
                        shape = RoundedCornerShape(12.dp),
                        colors = klipprFieldColors(isError = form.titleError),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(16.dp))

                    // Descripción
                    FieldLabel("Descripción *", isError = form.descriptionError)
                    OutlinedTextField(
                        value = form.description,
                        onValueChange = { form = form.copy(description = it, descriptionError = false) },
                        placeholder = { Text("Describe los detalles de la promoción...", color = TextGray, fontSize = 14.sp) },
                        minLines = 4,
                        isError = form.descriptionError,
                        shape = RoundedCornerShape(12.dp),
                        colors = klipprFieldColors(isError = form.descriptionError),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(16.dp))

                    // Descuento + Categoría
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel("Descuento *")
                            OutlinedTextField(
                                value = form.discountValue,
                                onValueChange = { form = form.copy(discountValue = it) },
                                placeholder = { Text("Ej: 50% OFF", color = TextGray, fontSize = 14.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = klipprFieldColors(),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel("Categoría *")
                            ExposedDropdownMenuBox(
                                expanded = categoryExpanded,
                                onExpandedChange = { categoryExpanded = it },
                            ) {
                                OutlinedTextField(
                                    value = form.category.displayName(),
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = klipprFieldColors(),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false },
                                ) {
                                    PromotionCategory.entries.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.displayName(), fontSize = 14.sp) },
                                            onClick = {
                                                form = form.copy(category = cat)
                                                categoryExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Fecha de expiración
                    FieldLabel("Fecho de Expiración *")
                    OutlinedTextField(
                        value = form.endDate,
                        onValueChange = { form = form.copy(endDate = it) },
                        placeholder = { Text("dd/mm/yy", color = TextGray, fontSize = 14.sp) },
                        singleLine = true,
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = TextGray)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = klipprFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── B. Condiciones de Uso ─────────────────────────────────────────
            item {
                SectionHeader(
                    title = "Condiciones de Uso",
                    action = {
                        Button(
                            onClick = {
                                form = form.copy(conditions = form.conditions + ConditionItem())
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = Color.White,
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    },
                )
            }

            if (form.conditions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .dashedBorder(DashColor)
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "No hay condiciones ¡Agregar algunas!",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                        )
                    }
                }
            } else {
                itemsIndexed(form.conditions, key = { _, item -> item.id }) { index, condition ->
                    ConditionItemRow(
                        item = condition,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        onTypeChange = { type ->
                            form = form.copy(conditions = form.conditions.mapIndexed { i, c ->
                                if (i == index) c.copy(type = type, dropdownExpanded = false) else c
                            })
                        },
                        onValueChange = { value ->
                            form = form.copy(conditions = form.conditions.mapIndexed { i, c ->
                                if (i == index) c.copy(value = value) else c
                            })
                        },
                        onDelete = {
                            form = form.copy(conditions = form.conditions.filterIndexed { i, _ -> i != index })
                        },
                        onExpandedChange = { expanded ->
                            form = form.copy(conditions = form.conditions.mapIndexed { i, c ->
                                if (i == index) c.copy(dropdownExpanded = expanded) else c.copy(dropdownExpanded = false)
                            })
                        },
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            // ── C. Código QR ──────────────────────────────────────────────────
            item {
                SectionHeader(title = "Codigo QR")
            }

            item {
                QrCodeSection(
                    qrCode = form.qrCode,
                    onRefresh = { form = form.copy(qrCode = generateQrCode()) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(24.dp))
            }

            // ── D. Acciones ───────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, KlipprPurple),
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        Text("Cancelar", color = Color(0xFFCCAACF), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                    Button(
                        onClick = {
                            val titleOk = form.title.isNotBlank()
                            val descOk = form.description.isNotBlank()
                            if (titleOk && descOk) {
                                onBack()
                            } else {
                                form = form.copy(titleError = !titleOk, descriptionError = !descOk)
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        Text("Crear", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    action: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(22.dp)
                    .background(KlipprPurple, RoundedCornerShape(2.dp)),
            )
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = KlipprPurple)
        }
        action?.invoke()
    }
}

@Composable
private fun FieldLabel(text: String, isError: Boolean = false) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = if (isError) ErrorRed else KlipprPurple,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
    )
}

/** Colores estándar para los campos de formulario Klippr: fondo lavanda, sin borde salvo error. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun klipprFieldColors(isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = KlipprLavender,
    focusedContainerColor = KlipprLavender,
    disabledContainerColor = KlipprLavender,
    unfocusedBorderColor = if (isError) ErrorRed else Color.Transparent,
    focusedBorderColor = if (isError) ErrorRed else KlipprPurple,
    disabledBorderColor = Color.Transparent,
    errorBorderColor = ErrorRed,
    errorContainerColor = KlipprLavender,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConditionItemRow(
    item: ConditionItem,
    onTypeChange: (ConditionType) -> Unit,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .dashedBorder(DashColor)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ExposedDropdownMenuBox(
                expanded = item.dropdownExpanded,
                onExpandedChange = onExpandedChange,
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = item.type?.label ?: "Elige una opcion",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = item.dropdownExpanded) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = KlipprPurple,
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = item.dropdownExpanded,
                    onDismissRequest = { onExpandedChange(false) },
                ) {
                    ConditionType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.label, fontSize = 14.sp) },
                            onClick = { onTypeChange(type) },
                        )
                    }
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar condición",
                    tint = ErrorRed,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        OutlinedTextField(
            value = item.value,
            onValueChange = onValueChange,
            enabled = item.type != null,
            placeholder = { Text("Valor...", fontSize = 12.sp, color = TextGray) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFEEEEEE),
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = KlipprPurple,
                disabledBorderColor = Color.Transparent,
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun QrCodeSection(
    qrCode: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KlipprLavender),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Icon(
                        Icons.Default.QrCode2,
                        contentDescription = "Código QR",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(160.dp)
                            .padding(16.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(qrCode, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            }
            // Refresh button top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Regenerar QR",
                        tint = TextGray,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateBottomBar(onNavigateToHome: () -> Unit) {
    val inactive = TextGray
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavigationBarItem(
            selected = true, onClick = {},
            icon = { Icon(Icons.Default.Apps, contentDescription = "+ QR") },
            label = { Text("+ QR", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KlipprPurple,
                selectedTextColor = KlipprPurple,
                indicatorColor = KlipprLavender,
            ),
        )
        NavigationBarItem(
            selected = false, onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactive,
                unselectedTextColor = inactive,
            ),
        )
        NavigationBarItem(
            selected = false, onClick = {},
            icon = { Icon(Icons.Default.Inbox, contentDescription = "Mi Lista") },
            label = { Text("Mi Lista", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = inactive,
                unselectedTextColor = inactive,
            ),
        )
    }
}

/** Borde dashed usando drawBehind; evita el border() estándar que no soporta pathEffect. */
private fun Modifier.dashedBorder(color: Color, cornerRadius: Dp = 12.dp): Modifier =
    this.drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)),
            ),
        )
    }

private fun PromotionCategory.displayName(): String = when (this) {
    PromotionCategory.FOOD -> "Comida"
    PromotionCategory.BEAUTY -> "Belleza"
    PromotionCategory.HEALTH -> "Salud"
    PromotionCategory.EDUCATION -> "Educación"
    PromotionCategory.ENTERTAINMENT -> "Entretenimiento"
    PromotionCategory.SPORTS -> "Deportes"
    PromotionCategory.SERVICES -> "Servicios"
    PromotionCategory.TECHNOLOGY -> "Tecnología"
    PromotionCategory.OTHER -> "General"
}
